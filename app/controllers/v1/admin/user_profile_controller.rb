class V1::Admin::UserProfileController < AdminController
  def index
    render status: 200, json: { success: true, message: 'success', data: Employee.current.as_json('ui_profile') }
  end

  def reset_password
    if params['current_password'].blank? || !Employee.current.authenticate(params['current_password'])
      render status: 400, json: { success: false, message: I18n.t('employee.password.update_failed'), 
        reason: { current_password: [I18n.t('validation.invalid', param: 'password')] } }
      return
    end

    if params['new_password'].blank?
      render status: 400, json: { success: false, message: I18n.t('employee.password.update_failed'), 
        reason: { password: [I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH)] } }
      return
    end

    employee = Employee.current
    employee.update(password: params['new_password'].to_s, password_confirmation: params['confirm_password'].to_s)
    if employee.errors.any?
      render status: 400, json: { success: false, message: I18n.t('employee.password.update_failed'), reason: employee.errors }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  def enable_two_factor
    if Employee.current.two_fa_enabled
      render status: 400, json: { success: false, message: I18n.t('employee.two_factor.already_enabled') }
      return
    end

    Employee.current.update!(two_fa_enabled: true)
    render status: 200, json: { success: true, message: I18n.t('employee.two_factor.enabled_successfully') }
  end

  def disable_two_factor
    unless Employee.current.two_fa_enabled
      render status: 400, json: { success: false, message: I18n.t('employee.two_factor.already_disabled') }
      return
    end

    Employee.current.update!(two_fa_enabled: false)
    render status: 200, json: { success: true, message: I18n.t('employee.two_factor.disabled_successfully') }
  end
end
