class V2::Api::Auth::SignupController < V2::Api::AuthController
  before_action :validate_user_agent
  before_action :validate_params, except: :google
  before_action :signup, except: :google

  def password
  end

  def otp
  end

  def google
    customer = Customer.create(email: @payload['email'], auth_mode: Customer::AUTH_MODE['GOOGLE_AUTH'])
    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: customer.errors.messages }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE['GOOGLE_AUTH'] }, login_ip: request.ip, 
      user_agent: request.headers['User-Agent'])
    render status: 200, json: { success: true, message: I18n.t('customer.create_success'), data: { token: session.get_jwt_token } }
  end

  private

  def validate_user_agent
    if request.headers['User-Agent'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'User-Agent') }
      return
    end
  end

  def validate_params
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), 
        reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end
  end

  def signup
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('customer.create_failed'), 
        reason: { otp: [ I18n.t('customer.param_expired', param: 'OTP') ] } }
      return
    end

    customer = Customer.new(token['param'] => token['value'], auth_mode: Customer::AUTH_MODE["#{params['action'].upcase}_AUTH"])
    customer.password = params['password'] if customer.auth_mode == Customer::AUTH_MODE['PASSWORD_AUTH']
    customer.save
    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: customer.errors.messages }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE["#{params['action'].upcase}_AUTH"] }, 
      login_ip: request.ip, user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('customer.create_success'), data: { token: session.get_jwt_token } }
  end
end
