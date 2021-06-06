class Platform::AuthController < PlatformController
  skip_before_action :authenticate_request, except: [:logout, :verify_otp]

  def login
    begin
      raise I18n.t('validation.required', param: 'Email') if params['email'].blank?

      if params['password'].to_s.length < PASSWORD_MIN_LENGTH
        render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
          password: [I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)] } }
        return
      end

      platform_user = PlatformUser.find_by_email(params['email'])
      if platform_user.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: I18n.t('auth.user.not_found') }
        return
      end

      raise I18n.t('auth.user.account_blocked', platform: PlatformConfig['name']) if platform_user.is_blocked?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { email: [e.message] } }
      return
    end

    unless platform_user.authenticate(params['password'])
      render status: 401, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [I18n.t('validation.invalid', param: 'password') ] } }
      return
    end

    session = platform_user.platform_user_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    if platform_user.two_fa_enabled
      data = { token: session.get_jwt_token({ 'status' => PlatformUser::TWO_FA_STATUSES['UNVERIFIED'],
        'auth_token' => PlatformUser.send_otp({ param: 'mobile', value: platform_user.mobile }) }), redirect_to: 'OTP' }
      message = I18n.t('platform_user.mobile_otp_success')
    else
      data = { token: session.get_jwt_token({ 'status' => PlatformUser::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' }
      message = I18n.t('auth.login_success')
    end

    render status: 200, json: { success: true, message: message, data: data }
  end

  def verify_otp
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['token'] != @payload['two_fa']['auth_token'] || token['code'] != params['otp']
      render status: 400, json: { success: false, message: I18n.t('auth.otp.verify_failed'), 
        reason: { otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    @payload['two_fa']['status'] = PlatformUser::TWO_FA_STATUSES['VERIFIED']
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] })
    
    render status: 200, json: { success: true, message: I18n.t('auth.login_success'), 
      data: { token: PlatformUser.current.platform_user_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
  end

  def reset_password
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    end

    begin
      raise I18n.t('validation.required', param: 'OTP') if params['otp'].blank?
      token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
      raise I18n.t('validation.param_expired', param: 'OTP') if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']

      platform_user = PlatformUser.where(token['param'] => token['value']).first
      if platform_user.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { token['param'] => [I18n.t('auth.user.not_found')] } }
        return
      end
      raise I18n.t('auth.account_blocked', platform: PlatformConfig['name']) if platform_user.is_blocked?

      platform_user.update(password: params['password'], password_confirmation: params['password_confirmation'])
      if platform_user.errors.any?
        render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: platform_user.errors.messages }
        return
      end

      Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { otp: [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  def logout
    PlatformUser.current.platform_user_sessions.find_by_token(PlatformUserSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end
end
