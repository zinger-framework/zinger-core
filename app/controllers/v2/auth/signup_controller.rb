class V2::Auth::SignupController < V2::AuthController
  def create
    self.send(AUTH_TYPES[@auth_type])
  end

  private
  def password_auth
    required_keys = OTP_PARAMS + ['user_name']
    keys_present = required_keys.select { |key| params[key].present? }
    if keys_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.required', param: required_keys.join(', ')) }
      return
    end

    if params['password'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('validation.required', param: 'Password') ] } }
      return
    elsif params['password'].to_s.length < User::PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('user.password.invalid', length: User::PASSWORD_MIN_LENGTH) ] } }
      return
    end

    user = User.create(keys_present[0] => params[keys_present[0]], password: params['password'], verified: false, two_factor_enabled: false)
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: user.errors.messages }
      return
    end

    session = user.user_sessions.create!(meta: { type: @auth_type }, login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('user.create_success'), data: { token: session.get_jwt_token } }
  end

  def otp_auth
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    end

    if params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('user.create_failed'), reason: { otp: [ I18n.t('user.param_expired', param: 'OTP') ] } }
      return
    end

    keys_present = OTP_PARAMS.select { |key| token[key].present? }
    user = User.create(keys_present[0] => token[keys_present[0]], verified: true, two_factor_enabled: false)
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: user.errors.messages }
      return
    end

    session = user.user_sessions.create!(meta: { type: @auth_type }, login_ip: request.ip, user_agent: params['user_agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('user.create_success'), data: { token: session.get_jwt_token } }
  end

  def google_auth
    if params['email'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { email: [ I18n.t('validation.required', param: 'Email address') ] } }
      return
    end

    user = User.create(email: params['email'], verified: true, two_factor_enabled: false)
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: user.errors.messages }
      return
    end

    session = user.user_sessions.create!(meta: { type: @auth_type }, login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('user.create_success'), data: { token: session.get_jwt_token } }
  end
end
