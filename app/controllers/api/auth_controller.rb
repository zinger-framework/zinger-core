class Api::AuthController < ApiController
  AUTH_PARAMS = %w(email mobile)
  AUTH_PURPOSES = %w(PASSWORD OTP GOOGLE)
  OTP_PURPOSES = %w(SIGNUP LOGIN RESET_PASSWORD VERIFY_ACCOUNT)

  skip_before_action :authenticate_request, except: [:logout, :otp], unless: -> { params['action'] == 'logout' || params['purpose'] == 'VERIFY_ACCOUNT' }

  def otp
    begin
      raise I18n.t('validation.required', param: 'Purpose') if params['purpose'].blank?
      raise I18n.t('validation.invalid', param: 'purpose') unless OTP_PURPOSES.include? params['purpose']
      params_present = AUTH_PARAMS.select { |key| params[key].present? }
      raise I18n.t('validation.too_many_params', param: AUTH_PARAMS.join(', ')) if params_present.length != 1
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: e.message }
      return
    end
    
    begin
      key = params_present.first
      case key
      when 'email'
        raise I18n.t('validation.invalid', param: 'email address') unless params['email'].match(EMAIL_REGEX)
      when 'mobile'
        raise I18n.t('validation.invalid', param: 'mobile number') unless params['mobile'].match(MOBILE_REGEX)
      end

      options = { param: key, value: params[key] }
      case params['purpose']
      when 'SIGNUP', 'VERIFY_ACCOUNT'
        raise I18n.t('auth.already_exist', key: key, value: params[key]) if Customer.exists?(key => params[key])
        options[:customer_id] = Customer.current.id if params['purpose'] == 'VERIFY_ACCOUNT'
      when 'LOGIN', 'RESET_PASSWORD'
        customer = Customer.where(key => params[key]).first
        if customer.blank? || (params['purpose'] == 'LOGIN' && !PlatformConfig['flexible_auth'] && customer.auth_mode != Customer::AUTH_MODE['OTP_AUTH']) ||
            (params['purpose'] == 'RESET_PASSWORD' && customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH'])
          render status: 404, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { key => [I18n.t('auth.user.not_found')] } }
          return
        elsif customer.is_blocked?
          render status: 403, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { 
            key => [I18n.t('auth.account_blocked', platform: PlatformConfig['name'])] } }
          return
        end
      end
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { key => [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.otp.success'), data: { auth_token: Customer.send_otp(options) } }
  end

  def signup
    begin
      raise I18n.t('validation.required', param: 'User-Agent') if request.headers['User-Agent'].blank?
      raise I18n.t('validation.required', param: 'Purpose') if params['purpose'].blank?
      raise I18n.t('validation.invalid', param: 'purpose') unless AUTH_PURPOSES.include? params['purpose']
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), reason: e.message }
      return
    end

    case params['purpose']
    when 'GOOGLE'
      email = verify_id_token params['id_token']
      return if email.class != String
      customer = Customer.create(email: email, auth_mode: Customer::AUTH_MODE['GOOGLE_AUTH'])
    when 'PASSWORD', 'OTP'
      if params['auth_token'].blank?
        render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), 
          reason: I18n.t('validation.required', param: 'Authentication token') }
        return
      elsif params['otp'].blank?
        render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), reason: { 
          otp: [ I18n.t('validation.required', param: 'OTP') ] } }
        return
      end

      token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
      if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
        render status: 401, json: { success: false, message: I18n.t('auth.signup_failed'), reason: { 
          otp: [ I18n.t('validation.param_expired', param: 'OTP') ] } }
        return
      end

      data = { token['param'] => token['value'], auth_mode: Customer::AUTH_MODE["#{params['purpose']}_AUTH"] }
      data = data.merge({ password: params['password'], password_confirmation: params['password_confirmation'] }) if params['purpose'] == 'PASSWORD'
      customer = Customer.create(data)
    end

    if customer.errors.any?
      render status: 400, json: { success: false, message: I18n.t('auth.signup_failed'), reason: customer.errors.messages }
      return
    end

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE["#{params['purpose']}_AUTH"] }, 
      login_ip: request.ip, user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }) if params['auth_token'].present?
    render status: 200, json: { success: true, message: I18n.t('auth.signup_success'), data: { token: session.get_jwt_token } }
  end

  def login
    begin
      raise I18n.t('validation.required', param: 'User-Agent') if request.headers['User-Agent'].blank?
      raise I18n.t('validation.required', param: 'Purpose') if params['purpose'].blank?
      raise I18n.t('validation.invalid', param: 'purpose') unless AUTH_PURPOSES.include? params['purpose']
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: e.message }
      return
    end

    customer = self.send("login_with_#{params['purpose'].downcase}")
    return if customer.class != Customer

    session = customer.customer_sessions.create!(meta: { auth_mode: CustomerSession::AUTH_MODE["#{params['purpose']}_AUTH"] }, 
      login_ip: request.ip, user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }) if params['auth_token'].present?
    render status: 200, json: { success: true, message: I18n.t('auth.login_success'), data: { token: session.get_jwt_token } }
  end

  def logout
    Customer.current.customer_sessions.find_by_token(CustomerSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  def reset_password
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), 
        reason: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        otp: [ I18n.t('validation.param_expired', param: 'OTP') ] } }
      return
    end
    
    customer = Customer.where(token['param'] => token['value']).first
    if customer.nil? || customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH']
      render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: {
        token['param'] => [I18n.t('auth.user.not_found')] } }
      return
    elsif customer.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { 
        token['param'] => [I18n.t('auth.account_blocked', platform: PlatformConfig['name'])] } }
      return
    end

    customer.update(password: params['password'], password_confirmation: params['password_confirmation'])
    if customer.errors.any?
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: customer.errors.messages }
      return
    end

    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  private

  def login_with_password
    params_present = AUTH_PARAMS.select { |key| params[key].present? }
    if params_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), 
        reason: I18n.t('validation.too_many_params', param: AUTH_PARAMS.join(', ')) }
      return
    end

    if params['password'].to_s.length < PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [ I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH) ] } }
      return
    end
    
    key = params_present.first
    customer = Customer.where(key => params[key]).first
    if customer.nil? || customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH']
      render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        key => [ I18n.t('auth.user.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        key => [ I18n.t('auth.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    elsif !customer.authenticate(params['password'])
      render status: 401, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        password: [ I18n.t('validation.invalid', param: 'password') ] } }
      return
    end
    return customer
  end

  def login_with_otp
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), 
        reason: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        otp: [ I18n.t('validation.param_expired', param: 'OTP') ] } }
      return
    end

    customer = Customer.where(token['param'] => token['value']).first
    if customer.nil? || (!PlatformConfig['flexible_auth'] && customer.auth_mode != Customer::AUTH_MODE['OTP_AUTH'])
      render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        key => [ I18n.t('auth.user.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        key => [ I18n.t('auth.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    end
    return customer
  end

  def login_with_google
    email = verify_id_token params['id_token']
    return if email.class != String
    customer = Customer.where(email: email).first
    if customer.nil? || (!PlatformConfig['flexible_auth'] && customer.auth_mode != Customer::AUTH_MODE['GOOGLE_AUTH'])
      render status: 404, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        email: [ I18n.t('auth.user.not_found') ] } }
      return
    elsif customer.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.login_failed'), reason: { 
        email: [ I18n.t('auth.account_blocked', platform: PlatformConfig['name']) ] } }
      return
    end
    return customer
  end
  
  def verify_id_token id_token
    if id_token.blank?
      render status: 400, json: { success: false, message: I18n.t('validation.invalid_request'), 
        reason: I18n.t('validation.required', param: 'id_token') }
      return
    end

    begin
      raise I18n.t('validation.param_expired', param: 'Token') if Core::Redis.fetch(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: id_token }) { false }
      Core::Redis.setex(Core::Redis::ID_TOKEN_VERIFICATION % { id_token: id_token }, true, 1.hour.to_i)
      validator = GoogleIDToken::Validator.new
      return validator.check(id_token, AppConfig['google_client_id']).to_h['email']
    rescue RuntimeError, GoogleIDToken::ValidationError => e
      render status: 401, json: { success: false, message: I18n.t('validation.invalid_request'), reason: e.message }
      return
    end
  end
end
