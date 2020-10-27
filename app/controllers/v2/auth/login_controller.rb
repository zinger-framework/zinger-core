class V2::Auth::LoginController < V2::AuthController
  before_action :validate_user_agent

  def password
    params_present = AUTH_PARAMS.select { |key| params[key].present? }
    if params_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.required', param: AUTH_PARAMS.join(', ')) }
      return
    end

    error_msg = if params['password'].blank?
      I18n.t('validation.required', param: 'Password')
    elsif params['password'].to_s.length < Customer::PASSWORD_MIN_LENGTH
      I18n.t('customer.password.invalid', length: Customer::PASSWORD_MIN_LENGTH)
    end

    if error_msg.present?
      render status: 400, json: { success: false, message: I18n.t('customer.login_failed'), reason: { password: [error_msg] } }
      return
    end
    
    key = params_present.first
    customer = Customer.where(key => params[key]).first
    if customer.nil? || customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH']
      render status: 404, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { key => [ I18n.t('customer.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 401, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { key => [ I18n.t('customer.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    elsif customer.authenticate(params['password']) == false
      render status: 401, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { password: [ I18n.t('validation.invalid', param: 'Password') ] } }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE['PASSWORD_AUTH'] }, login_ip: request.ip, 
      user_agent: request.headers['User-Agent'])
    render status: 200, json: { success: true, message: I18n.t('customer.login_success'), data: { token: session.get_jwt_token } }
  end

  def otp
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { otp: [ I18n.t('customer.param_expired', param: 'OTP') ] } }
      return
    end

    customer = Customer.where(token['param'] => token['value']).first
    if customer.nil? || (!PlatformConfig['flexible_auth'] && customer.auth_mode != Customer::AUTH_MODE['OTP_AUTH'])
      render status: 404, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { key => [ I18n.t('customer.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { key => [ I18n.t('customer.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE['OTP_AUTH'] }, login_ip: request.ip, 
      user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('customer.login_success'), data: { token: session.get_jwt_token } }
  end

  def google
    customer = Customer.where(email: @payload['email']).first
    if customer.nil? || (!PlatformConfig['flexible_auth'] && customer.auth_mode != Customer::AUTH_MODE['GOOGLE_AUTH'])
      render status: 404, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { email: [ I18n.t('customer.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 400, json: { success: false, message: I18n.t('customer.login_failed'), 
        reason: { email: [ I18n.t('customer.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE['GOOGLE_AUTH'] }, login_ip: request.ip, 
      user_agent: request.headers['User-Agent'])
    render status: 200, json: { success: true, message: I18n.t('customer.login_success'), data: { token: session.get_jwt_token } }
  end

  private

  def validate_user_agent
    if request.headers['User-Agent'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'User-Agent') }
      return
    end
  end
end
