class Admin::UserProfileController < AdminController
  def index
    render status: 200, json: { success: true, message: 'success', data: Employee.current.as_json('ui_profile') }
  end

  def modify
    Employee.current.name = params['name'] if params['name'].present?
    if params['auth_token'].present?
      token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
      if token.blank? || token['token'] != params['auth_token'] || token['code'] != params['otp'] || token['employee_id'].to_i != Employee.current.id
        render status: 400, json: { success: false, message: I18n.t('profile.update_failed'), 
          reason: { otp: [I18n.t('validation.param_expired', param: 'OTP')] } }
        return
      end
      Employee.current.mobile = token['value']
    end
    Employee.current.two_fa_enabled = params['two_fa_enabled'].to_s == 'true' unless params['two_fa_enabled'].nil?
    Employee.current.save

    if Employee.current.errors.any?
      render status: 400, json: { success: false, message: I18n.t('profile.update_failed'), reason: Employee.current.errors }
      return
    end

    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }) if params['auth_token'].present?
    render status: 200, json: { success: true, message: I18n.t('profile.update_success'), data: Employee.current.as_json('ui_profile') }
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
end
