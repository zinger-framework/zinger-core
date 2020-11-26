class AdminController < ApplicationController
  before_action :reset_thread, :set_title, :authenticate_request, :check_limit
  AUTHORIZED_2FA_STATUSES = [Employee::TWO_FA_STATUSES['NOT_APPLICABLE'], Employee::TWO_FA_STATUSES['VERIFIED']]

  def dashboard
    @title = 'Dashboard'
  end

  private

  def set_title
    @header = { links: [] }
  end

  def authenticate_request
    employee, payload = session[:authorization].present? ? EmployeeSession.fetch_employee(session[:authorization]) : nil
    if employee.nil?
      session.delete(:authorization)
      flash[:warn] = 'Please login to continue'
      return redirect_to auth_index_path
    end

    if employee.two_fa_enabled && payload['two_fa']['status'] != Employee::TWO_FA_STATUSES['VERIFIED'] &&
        "#{params['controller']}##{params['action']}" != 'admin/auth#logout'
      flash[:warn] = 'Please verify OTP to continue'
      return redirect_to otp_auth_index_path
    end
    
    employee.make_current
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      flash[:error] = resp
      return redirect_to request.referrer
    end
  end

  def reset_thread
    Employee.reset_current
  end
end
