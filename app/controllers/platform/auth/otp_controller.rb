class Platform::Auth::OtpController < Platform::AuthController
  before_action :authenticate_request, only: [:login, :verify_mobile]

  def login
    @payload['two_fa']['auth_token'] = PlatformUser.send_otp({ param: 'mobile', value: PlatformUser.current.mobile })
    render status: 200, json: { success: true, message: I18n.t('platform_user.mobile_otp_success'), 
      data: { token: PlatformUser.current.platform_user_sessions.find_by_token(@payload['token']).get_jwt_token(@payload['two_fa']) } }
  end

  def forgot_password
    begin
      raise I18n.t('validation.required', param: 'Email address') if params['email'].blank?
      raise I18n.t('validation.invalid', param: 'email address') if params['email'].match(EMAIL_REGEX).nil?

      platform_user = PlatformUser.find_by_email(params['email'])
      if platform_user.nil?
        render status: 404, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { email: [ I18n.t('auth.user.not_found') ] } }
        return
      end
      raise I18n.t('auth.account_blocked', platform: PlatformConfig['name']) if platform_user.is_blocked?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { email: [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.otp.success'), 
      data: { auth_token: PlatformUser.send_otp({ param: 'email', value: params['email'] }) } }
  end

  def verify_mobile
    begin
      raise I18n.t('validation.required', param: 'Mobile number') if params['mobile'].blank?
      raise I18n.t('validation.invalid', param: 'mobile number') if params['mobile'].match(MOBILE_REGEX).nil?
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('auth.otp.failed'), reason: { mobile: [e.message] } }
      return
    end

    render status: 200, json: { success: true, message: I18n.t('auth.otp.success'), 
      data: { auth_token: PlatformUser.send_otp({ param: 'mobile', value: params['mobile'], platform_user_id: PlatformUser.current.id }) } }
  end
end
