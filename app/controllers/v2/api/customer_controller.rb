class V2::Api::CustomerController < ApiController
  def profile
    render status: 200, json: { success: true, message: 'success', data: Customer.current.as_json('ui_profile') }
  end

  def update_profile
    Customer.current.update(name: params['name'])
    if Customer.current.errors.any?
      render status: 400, json: { success: false, message: I18n.t('profile.update_failed'), reason: Customer.current.errors.messages }
      return
    end
    render status: 200, json: { success: true, message: I18n.t('profile.update_success'), data: Customer.current.as_json('ui_profile') }
  end

  def reset_profile
    if params['auth_token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication token') }
      return
    elsif params['otp'].blank?
      render status: 400, json: { success: false, message: I18n.t('profile.reset_failed'),
        reason: { otp: [ I18n.t('validation.required', param: 'OTP') ] } }
      return
    end

    token = Core::Redis.fetch(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] }, { type: Hash }) { nil }
    if token.blank? || params['auth_token'] != token['token'] || token['customer_id'] != Customer.current.id || token['code'] != params['otp']
      render status: 401, json: { success: false, message: I18n.t('profile.reset_failed'),
        reason: { otp: [ I18n.t('validation.param_expired', param: 'OTP') ] } }
      return
    end

    Customer.current.update(token['param'] => token['value'])
    if Customer.current.errors.any?
      render status: 400, json: { success: false, message: I18n.t('profile.reset_failed'), reason: Customer.current.errors.messages }
      return
    end

    Core::Redis.delete(Core::Redis::OTP_VERIFICATION % { token: params['auth_token'] })
    render status: 200, json: { success: true, message: I18n.t('profile.reset_success'), data: Customer.current.as_json('ui_profile') }
  end

  def password
    if params['current_password'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), 
        reason: { current_password: [ I18n.t('validation.required', param: 'Current password') ] } }
      return
    end

    if Customer.current.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH'] || Customer.current.authenticate(params['current_password']) == false
      render status: 401, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'),
        reason: { current_password: [ I18n.t('validation.invalid', param: 'current password') ] } }
      return
    end

    Customer.current.update(password: params['password'])
    if Customer.current.errors.any?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: Customer.current.errors.messages }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end
end
