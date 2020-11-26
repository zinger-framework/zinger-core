class V2::Api::AuthController < ApiController
  AUTH_PARAMS = %w(email mobile)

  skip_before_action :authenticate_request, except: [:logout, :reset_profile]
  before_action :verify_auth_token, only: :google

  def logout
    Customer.current.customer_sessions.find_by_token(CustomerSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  def reset_password
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), 
        reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), 
        reason: { otp: [ I18n.t('validation.param_expired', param: 'OTP') ] } }
      return
    end
    
    customer = Customer.where(token['param'] => token['value']).first
    if customer.nil? || customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH']
      render status: 404, json: { success: false, message: I18n.t('customer.not_found') }
      return
    elsif customer.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('customer.account_blocked', platform: PlatformConfig['name']) }
      return
    end

    customer.update(password: params['password'])
    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: customer.errors.messages }
      return
    end

    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  private
  
  def verify_auth_token
    if params['id_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'id_token') }
      return
    end

    if Core::Redis.fetch(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: params['id_token'] }) { false }
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), 
        reason: I18n.t('validation.param_expired', param: 'Token') }
      return
    end
    Core::Redis.setex(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: params['id_token'] }, true, 1.hour.to_i)

    validator = GoogleIDToken::Validator.new
    begin
      @payload = validator.check(params['id_token'], AppConfig['google_client_id'])
    rescue GoogleIDToken::ValidationError => e
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: e }
      return
    end
  end
end
