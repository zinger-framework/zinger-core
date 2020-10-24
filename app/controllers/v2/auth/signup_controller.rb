class V2::Auth::SignupController < V2::AuthController
  before_action :validate_params, except: :google
  before_action :validate_password, only: :password
  before_action :signup, except: :google

  def password
  end

  def otp
  end

  def google
    customer = Customer.create(email: @payload['email'])
    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: customer.errors.messages }
      return
    end

    session = customer.customer_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    render status: 200, json: { success: true, message: I18n.t('customer.create_success'), data: { token: session.get_jwt_token } }
  end

  private

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

  def validate_password
    error_msg = if params['password'].blank?
      I18n.t('validation.required', param: 'Password')
    elsif params['password'].to_s.length < Customer::PASSWORD_MIN_LENGTH
      I18n.t('customer.password.invalid', length: Customer::PASSWORD_MIN_LENGTH)
    end

    if error_msg.present?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: { password: [error_msg] } }
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

    customer = Customer.new(token['param'] => token['value'])
    customer.password = params['password'] if params['action'] == 'password'
    customer.save
    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('customer.create_failed'), reason: customer.errors.messages }
      return
    end

    session = customer.customer_sessions.create!(login_ip: request.ip, user_agent: params['user_agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('customer.create_success'), data: { token: session.get_jwt_token } }
  end
end
