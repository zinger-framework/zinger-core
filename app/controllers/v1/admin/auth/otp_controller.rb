class V1::Admin::Auth::OtpController < V1::Admin::AuthController
  before_action :authenticate_request, only: :login

  def login
    @payload['two_fa']['auth_token'] = Employee.send_otp({ param: 'mobile', value: Employee.current.mobile })
    render status: 200, json: { success: true, message: I18n.t('employee.otp_success'), 
      data: { token: Employee.current.employee_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
  end

  def forgot_password
    begin
      raise I18n.t('validation.required', param: 'Email address') if params['email'].blank?
      raise I18n.t('validation.invalid', param: 'email address') if params['email'].match(EMAIL_REGEX).nil?

      employee = Employee.find_by_email(params['email'])
      if employee.nil?
        render status: 404, json: { success: false, message: I18n.t('employee.otp_failed'), reason: { email: [ I18n.t('employee.not_found') ] } }
        return
      end
      raise I18n.t('employee.account_blocked', platform: PlatformConfig['name']) if employee.is_blocked?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('employee.otp_failed'), reason: { email: [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('employee.otp_success'), 
      data: { auth_token: Employee.send_otp({ param: 'email', value: params['email'] }) } }
    return
  end
end
