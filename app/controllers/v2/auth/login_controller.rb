class V2::Auth::LoginController < V2::AuthController
  def password
    params_present = AUTH_PARAMS.select { |key| params[key].present? }
    if params_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.required', param: AUTH_PARAMS.join(', ')) }
      return
    elsif params['password'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { password: [ I18n.t('validation.required', param: 'Password') ] } }
      return
    elsif params['password'].to_s.length < User::PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { password: [ I18n.t('user.password.invalid', length: User::PASSWORD_MIN_LENGTH) ] } }
      return
    end
    
    key = params_present.first
    user = User.where(key => params[key]).first
    if user.nil? || user.password_digest.blank?
      render status: 404, json: { success: false, message: I18n.t('user.login_failed'), reason: { key => [ I18n.t('user.not_found') ] } }
      return
    elsif user.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { key => [ I18n.t('user.account_blocked') ] } }
      return
    elsif user.authenticate(params['password']) == false
      render status: 401, json: { success: false, message: I18n.t('user.login_failed'), reason: { password: [ I18n.t('validation.invalid', param: 'Password') ] } }
      return
    end

    session = user.user_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('user.login_success'), data: { token: session.get_jwt_token } }
  end

  def otp
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('user.login_failed'), reason: { otp: [ I18n.t('user.param_expired', param: 'OTP') ] } }
      return
    end

    user = User.where(token['param'] => token['value']).first
    if user.nil?
      render status: 404, json: { success: false, message: I18n.t('user.login_failed'), reason: { key => [ I18n.t('user.not_found') ] } }
      return
    elsif user.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { key => [ I18n.t('user.account_blocked') ] } }
      return
    end

    session = user.user_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('user.login_success'), data: { token: session.get_jwt_token } }
  end

  def google
    user = User.where(email: @payload['email']).first
    if user.nil?
      render status: 404, json: { success: false, message: I18n.t('user.login_failed'), reason: { email: [ I18n.t('user.not_found') ] } }
      return
    elsif user.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('user.login_failed'), reason: { email: [ I18n.t('user.account_blocked') ] } }
      return
    end

    session = user.user_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('user.login_success'), data: { token: session.get_jwt_token } }
  end
end
