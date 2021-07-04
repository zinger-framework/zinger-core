class Platform::AuthController < PlatformController
  AUTH_PARAMS = %w(email mobile)
  OTP_PURPOSES = %w(TWO_FA FORGOT_PASSWORD VERIFY_ACCOUNT)
  SKIP_AUTHENTICATE_PARAMS = %w(TWO_FA VERIFY_ACCOUNT)

  skip_before_action :authenticate_request, except: [:otp, :verify_otp, :logout], 
    unless: -> { params['action'] != 'otp' || SKIP_AUTHENTICATE_PARAMS.include?(params['purpose']) }

  def otp
    begin
      raise I18n.t('validation.required', param: 'Purpose') if params['purpose'].blank?
      raise I18n.t('validation.invalid', param: 'purpose') unless OTP_PURPOSES.include? params['purpose']
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: e.message }
      return
    end

    self.send("send_#{params['purpose'].downcase}_otp")
  end

  def login
    if params['email'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        email: [I18n.t('validation.required', param: 'Email')] } }
      return
    elsif params['password'].to_s.length < PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)] } }
      return
    end

    platform_user = PlatformUser.undeleted.find_by_email(params['email'])
    if platform_user.nil?
      render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        email: [I18n.t('auth.user.not_found')] } }
      return
    elsif platform_user.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.login_failed'), reason: {
        email: [I18n.t('auth.user.account_blocked', platform: PlatformConfig['name'])] } }
      return
    elsif !platform_user.authenticate(params['password'])
      render status: 401, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [I18n.t('validation.invalid', param: 'password') ] } }
      return
    end

    session = platform_user.platform_user_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    if platform_user.two_fa_enabled
      data = { token: session.get_jwt_token({ 'status' => PlatformUser::TWO_FA_STATUSES['UNVERIFIED'],
        'auth_token' => PlatformUser.send_otp({ param: 'mobile', value: platform_user.mobile }) }), redirect_to: 'OTP' }
      message = I18n.t('auth.two_factor.otp_success')
    else
      data = { token: session.get_jwt_token({ 'status' => PlatformUser::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' }
      message = I18n.t('auth.login_success')
    end

    render status: 200, json: { success: true, message: message, data: data }
  end

  def verify_otp
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['token'] != @payload['two_fa']['auth_token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.otp.verify_failed'), reason: { 
        otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    @payload['two_fa']['status'] = PlatformUser::TWO_FA_STATUSES['VERIFIED']
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] })
    
    render status: 200, json: { success: true, message: I18n.t('auth.login_success'), 
      data: { token: PlatformUser.current.platform_user_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
  end

  def reset_password
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), 
        reason: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        otp: [I18n.t('validation.required', param: 'OTP')] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    platform_user = PlatformUser.undeleted.where(token['param'] => token['value']).first
    if platform_user.nil?
      render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        token['param'] => [I18n.t('auth.user.not_found')] } }
      return
    elsif platform_user.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        token['param'] => [I18n.t('auth.account_blocked', platform: PlatformConfig['name'])] } }
      return
    end

    platform_user.update(password: params['password'], password_confirmation: params['password_confirmation'])
    if platform_user.errors.any?
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: platform_user.errors.messages }
      return
    end

    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  def logout
    PlatformUser.current.platform_user_sessions.find_by_token(PlatformUserSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  private

  def send_two_fa_otp
    @payload['two_fa']['auth_token'] = PlatformUser.send_otp({ param: 'mobile', value: PlatformUser.current.mobile })
    render status: 200, json: { success: true, message: I18n.t('auth.two_factor.otp_success'), 
      data: { token: PlatformUser.current.platform_user_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
  end

  def send_forgot_password_otp
    begin
      raise I18n.t('validation.required', param: 'Email address') if params['email'].blank?
      raise I18n.t('validation.invalid', param: 'email address') if params['email'].match(EMAIL_REGEX).nil?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { email: [e.message] } }
      return
    end

    platform_user = PlatformUser.undeleted.find_by_email(params['email'])
    if platform_user.nil?
      render status: 404, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { email: [ I18n.t('auth.user.not_found') ] } }
      return
    elsif platform_user.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { 
        email: [ I18n.t('auth.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.otp.success'), 
      data: { auth_token: PlatformUser.send_otp({ param: 'email', value: params['email'] }) } }
  end

  def send_verify_account_otp
    params_present = AUTH_PARAMS.select { |key| params[key].present? }
    if params_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), 
        reason: I18n.t('validation.too_many_params', param: AUTH_PARAMS.join(', ')) }
      return
    end

    begin
      key = params_present.first
      case key
      when 'email'
        raise I18n.t('validation.invalid', param: 'email address') unless params['email'].match(EMAIL_REGEX)
        raise I18n.t('auth.already_exist', key: 'email', value: params['email']) if PlatformUser.exists?(email: params['email'])
      when 'mobile'
        raise I18n.t('validation.invalid', param: 'mobile number') unless params['mobile'].match(MOBILE_REGEX)
      end
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { key => [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.otp.success'), 
      data: { auth_token: PlatformUser.send_otp({ param: key, value: params[key], platform_user_id: PlatformUser.current.id }) } }
  end
end
