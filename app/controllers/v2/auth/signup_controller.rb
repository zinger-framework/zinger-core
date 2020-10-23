class V2::Auth::SignupController < V2::AuthController
  before_action :validate_params, except: :google
  before_action :validate_password, only: :password
  before_action :signup, except: :google

  def password
  end

  def otp
  end

  def google
    user = User.create(email: @payload['email'], two_factor_enabled: false)
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: user.errors.messages }
      return
    end

    session = user.user_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('user.create_success'), data: { token: session.get_jwt_token } }
  end

  private

  def validate_params
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end
  end

  def validate_password
    if params['password'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('validation.required', param: 'Password') ] } }
      return
    elsif params['password'].to_s.length < User::PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('user.password.invalid', length: User::PASSWORD_MIN_LENGTH) ] } }
      return
    end
  end

  def signup
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('user.create_failed'), reason: { otp: [ I18n.t('user.param_expired', param: 'OTP') ] } }
      return
    end

    user = User.new(token['param'] => token['value'], two_factor_enabled: false)
    user.password = params['password'] if params['action'] == 'password'
    user.save
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: user.errors.messages }
      return
    end

    session = user.user_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('user.create_success'), data: { token: session.get_jwt_token } }
  end
end
