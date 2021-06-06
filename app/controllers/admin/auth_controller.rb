class Admin::AuthController < AdminController
  skip_before_action :authenticate_request, except: [:logout, :verify_otp]

  def login
    begin
      raise I18n.t('validation.required', param: 'Email') if params['email'].blank?

      if params['password'].to_s.length < PASSWORD_MIN_LENGTH
        render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
          password: [I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)] } }
        return
      end

      admin_user = AdminUser.find_by_email(params['email'])
      if admin_user.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: I18n.t('auth.user.not_found') }
        return
      end

      raise I18n.t('auth.account_blocked', platform: PlatformConfig['name']) if admin_user.is_blocked?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { email: [e.message] } }
      return
    end

    unless admin_user.authenticate(params['password'])
      render status: 401, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [I18n.t('validation.invalid', param: 'password') ] } }
      return
    end

    session = admin_user.admin_user_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    if admin_user.two_fa_enabled
      data = { token: session.get_jwt_token({ 'status' => AdminUser::TWO_FA_STATUSES['UNVERIFIED'],
        'auth_token' => AdminUser.send_otp({ param: 'mobile', value: admin_user.mobile }) }), redirect_to: 'OTP' }
      message = I18n.t('admin_user.mobile_otp_success')
    else
      data = { token: session.get_jwt_token({ 'status' => AdminUser::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' }
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

    @payload['two_fa']['status'] = AdminUser::TWO_FA_STATUSES['VERIFIED']
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] })
    
    render status: 200, json: { success: true, message: I18n.t('auth.login_success'), 
      data: { token: AdminUser.current.admin_user_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
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

      admin_user = AdminUser.where(token['param'] => token['value']).first
      if admin_user.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { token['param'] => [I18n.t('auth.user.not_found')] } }
        return
      end
      raise I18n.t('auth.account_blocked', platform: PlatformConfig['name']) if admin_user.is_blocked?

      admin_user.update(password: params['password'], password_confirmation: params['password_confirmation'])
      if admin_user.errors.any?
        render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: admin_user.errors.messages }
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
    AdminUser.current.admin_user_sessions.find_by_token(AdminUserSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  def signup
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['token'] != params['auth_token'] || token['code'] != params['otp']
      render status: 400, json: { success: false, message: I18n.t('auth.otp.verify_failed'), 
        reason: { otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    if AdminUser.find_by_email(token['value']).present?
      render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), 
        reason: I18n.t('auth.already_exist', key: 'email', value: token['value']) }
      return
    end
      
    admin_user = AdminUser.create({ email: token['value'], name: params['name'], password: params['password'], 
      password_confirmation: params['password_confirmation'] })
    if admin_user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), reason: admin_user.errors.messages }
      return
    end
    
    emp_session = admin_user.admin_user_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.signup_success'), data: { token: emp_session.get_jwt_token({ 
      'status' => AdminUser::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' } }
  end
end
