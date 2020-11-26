class Admin::AuthController < AdminController
  skip_before_action :authenticate_request, except: :logout
  before_action :go_to_dashboard, only: [:index, :login]
  before_action :verify_jwt_token, except: [:index, :login, :logout]

  def index
  end

  def login
    if params['email'].blank?
      flash[:error] = I18n.t('validation.required', param: 'Email')
      return redirect_to auth_index_path
    end

    if params['password'].to_s.length < PASSWORD_MIN_LENGTH
      flash[:error] = I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)
      return redirect_to auth_index_path
    end

    employee = Employee.find_by_email(params['email'])
    error_msg = if employee.nil?
      I18n.t('employee.not_found')
    elsif employee.is_blocked?
      I18n.t('employee.account_blocked', platform: PlatformConfig['name'])
    elsif employee.authenticate(params['password']) == false
      I18n.t('validation.invalid', param: 'password')
    end
    
    if error_msg.present?
      flash[:error] = error_msg
      return redirect_to auth_index_path
    end

    emp_session = employee.employee_sessions.create!(login_ip: request.ip, user_agent: request.headers['User-Agent'])
    if employee.two_fa_enabled
      session[:authorization] = emp_session.get_jwt_token({ 'status' => Employee::TWO_FA_STATUSES['UNVERIFIED'], 
        'auth_token' => Employee.send_otp({ param: 'mobile', value: employee.mobile }) })
      flash[:success] = I18n.t('employee.otp_success')
      redirect_to otp_auth_index_path
    else
      session[:authorization] = emp_session.get_jwt_token({ 'status' => Employee::TWO_FA_STATUSES['NOT_APPLICABLE'] })
      flash[:success] = I18n.t('employee.login_success')
      redirect_to dashboard_path
    end
  end

  def otp
  end

  def otp_login
    @token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] }, { type: Hash }) { nil }
    if @token.blank? || @token['token'] != @payload['two_fa']['auth_token'] || @token['code'] != params['otp']
      flash[:error] = I18n.t('validation.param_expired', param: 'OTP')
      return redirect_to otp_auth_index_path
    end

    @payload['two_fa']['status'] = Employee::TWO_FA_STATUSES['VERIFIED']
    session[:authorization] = @employee.employee_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa'])
    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: @payload['two_fa']['auth_token'] })
    flash[:success] = I18n.t('employee.login_success')
    redirect_to dashboard_path
  end

  def resend_otp
    @payload['two_fa']['auth_token'] = Employee.send_otp({ param: 'mobile', value: @employee.mobile })
    session[:authorization] = @employee.employee_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa'])
    flash[:success] = I18n.t('employee.otp_success')
    redirect_to otp_auth_index_path
  end

  def logout
    Employee.current.employee_sessions.find_by_token(EmployeeSession.extract_token(session[:authorization])).destroy!
    session.delete(:authorization)
    flash[:success] = I18n.t('auth.logout_success')
    redirect_to auth_index_path
  end

  private

  def go_to_dashboard
    return redirect_to dashboard_path if session[:authorization].present?
  end

  def verify_jwt_token
    @employee, @payload = session[:authorization].present? ? EmployeeSession.fetch_employee(session[:authorization]) : nil
    if @employee.nil?
      session.delete(:authorization)
      flash[:warn] = 'Please login to continue'
      return redirect_to auth_index_path
    end

    return redirect_to dashboard_path if AUTHORIZED_2FA_STATUSES.include? @payload['two_fa']['status']
  end
end
