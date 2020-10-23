class V2::AuthController < ApiController
  AUTH_PARAMS = %w(email mobile)

  skip_before_action :authenticate_request, except: :logout
  before_action :verify_auth_token, only: :google

  def logout
    User.current.user_sessions.find_by_token(UserSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  def reset_password
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    elsif params['password'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { password: [ I18n.t('validation.required', param: 'Password') ] } }
      return
    elsif params['password'].to_s.length < User::PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { password: [ I18n.t('user.password.invalid', length: User::PASSWORD_MIN_LENGTH) ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { otp: [ I18n.t('user.param_expired', param: 'OTP') ] } }
      return
    end
    
    user = User.where(token['param'] => token['value']).first
    if user.nil?
      render status: 404, json: { success: false, message: I18n.t('user.not_found') }
      return
    elsif user.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('user.account_blocked') }
      return
    end

    user.update!(password: params['password'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  private
  def verify_auth_token
    if Core::Redis.fetch(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: params['id_token'] }) { false }
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: I18n.t('user.param_expired', param: 'Token') }
      return
    end
    Core::Redis.setex(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: params['id_token'] }, true, 1.hour.to_i)

    validator = GoogleIDToken::Validator.new
    begin
      @payload = validator.check(params['id_token'], AppConfig['google_client_id'], AppConfig['google_client_id'])
    rescue GoogleIDToken::ValidationError => e
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: e }
      return
    end
  end
end
