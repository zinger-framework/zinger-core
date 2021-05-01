class V1::Admin::AuthController < AdminController
  skip_before_action :authenticate_request, except: [:logout, :verify_otp]

  def login
    error = if params['user_type'].blank?
      I18n.t('validation.required', param: 'User type') 
    elsif !%w(Admin Employee).include? params['user_type']
      I18n.t('validation.invalid', param: 'user type')
    end

    if error.present?
      render status: 400, json: { success: false, message: I18n.t('employee.login_failed'), reason: { user_type: [error] } }
      return
    end

    begin
      raise I18n.t('validation.required', param: 'Email') if params['email'].blank?

      if params['password'].to_s.length < PASSWORD_MIN_LENGTH
        render status: 400, json: { success: false, message: I18n.t('employee.login_failed'), reason: { 
          password: [I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)] } }
        return
      end

      employee = case params['user_type']
      when 'Employee'
        Employee.find_by_email(params['email'])
      end

      if employee.nil?
        render status: 404, json: { success: false, message: I18n.t('employee.login_failed'), reason: I18n.t('employee.not_found') }
        return
      end

      raise I18n.t('employee.account_blocked', platform: PlatformConfig['name']) if employee.is_blocked?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('employee.login_failed'), reason: { email: [e.message] } }
      return
    end

    unless employee.authenticate(params['password'])
      render status: 401, json: { success: false, message: I18n.t('employee.login_failed'), reason: { 
        password: [I18n.t('validation.invalid', param: 'password') ] } }
      return
    end

    emp_session = employee.employee_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    if employee.two_fa_enabled
      data = { token: emp_session.get_jwt_token({ 'status' => Employee::TWO_FA_STATUSES['UNVERIFIED'],
        'auth_token' => Employee.send_otp({ param: 'mobile', value: employee.mobile }) }), redirect_to: 'OTP' }
      message = I18n.t('employee.mobile_otp_success')
    else
      data = { token: emp_session.get_jwt_token({ 'status' => Employee::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' }
      message = I18n.t('employee.login_success')
    end

    render status: 200, json: { success: true, message: message, data: data }
  end

  def verify_otp
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['token'] != @payload['two_fa']['auth_token'] || token['code'] != params['otp']
      render status: 400, json: { success: false, message: I18n.t('employee.otp_verify_failed'), 
        reason: { otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    @payload['two_fa']['status'] = Employee::TWO_FA_STATUSES['VERIFIED']
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] })
    
    render status: 200, json: { success: true, message: I18n.t('employee.login_success'), 
      data: { token: Employee.current.employee_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
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

      employee = Employee.where(token['param'] => token['value']).first
      if employee.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { token['param'] => [I18n.t('employee.not_found')] } }
        return
      end
      raise I18n.t('employee.account_blocked', platform: PlatformConfig['name']) if employee.is_blocked?

      employee.update(password: params['password'], password_confirmation: params['password_confirmation'])
      if employee.errors.any?
        render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: employee.errors.messages }
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
    Employee.current.employee_sessions.find_by_token(EmployeeSession.extract_token(request.headers['Authorization'])).destroy!
    render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
  end

  def signup
    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || token['token'] != params['auth_token'] || token['code'] != params['otp']
      render status: 400, json: { success: false, message: I18n.t('employee.otp_verify_failed'), 
        reason: { otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
      return
    end

    if Employee.find_by_email(token['value']).present?
      render status: 400, json: { success: false, message: I18n.t('employee.signup_failed'), 
        reason: I18n.t('auth.already_exist', key: 'email', value: token['value']) }
      return
    end
      
    employee = Employee.create({ email: token['value'], name: params['name'], password: params['password'], 
      password_confirmation: params['password_confirmation'] })
    if employee.errors.any?
      render status: 400, json: { success: false, message: I18n.t('employee.signup_failed'), reason: employee.errors.messages }
      return
    end
    
    emp_session = employee.employee_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('employee.signup_success'), data: { token: emp_session.get_jwt_token({ 
      'status' => Employee::TWO_FA_STATUSES['NOT_APPLICABLE'] }), redirect_to: 'DASHBOARD' } }
  end
end
