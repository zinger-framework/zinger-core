class AdminController < ApplicationController
  before_action :reset_thread, :authenticate_request, :check_limit, :check_version
  TWO_FACTOR_SCREENS = ['v1/admin/auth#verify_otp', 'v1/admin/auth/otp#login']

  private

  def authenticate_request
    employee, @payload = EmployeeSession.fetch_employee(request.headers['Authorization'])
    error_msg = if employee.nil?
      I18n.t('validation.invalid', param: 'authorization')
    elsif employee.is_blocked?
      I18n.t('employee.account_blocked', platform: PlatformConfig['name'])
    end
    
    if error_msg.present?
      render status: 401, json: { success: false, message: error_msg, reason: 'UNAUTHORIZED' }
      return
    end

    employee.make_current

    if TWO_FACTOR_SCREENS.include?("#{params['controller']}##{params['action']}")
      if !employee.two_fa_enabled
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('employee.two_factor.already_disabled') }
        return
      elsif @payload['two_fa']['status'] != Employee::TWO_FA_STATUSES['UNVERIFIED']
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('validation.otp.already_verified') }
        return
      end
    elsif params['action'] != 'logout' && employee.two_fa_enabled && @payload['two_fa']['status'] != Employee::TWO_FA_STATUSES['VERIFIED']
      render status: 401, json: { success: false, message: I18n.t('validation.otp.unverified'), reason: 'OTP_UNVERIFIED' }
      return
    end
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      render status: 429, json: { success: false, message: resp }
      return
    end
  end

  def check_version
    raise VersionCake::ObsoleteVersionError.new '' if request_version.to_i != 1
  end

  def reset_thread
    Employee.reset_current
  end
end
