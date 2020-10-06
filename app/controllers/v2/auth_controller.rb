class V2::AuthController < ApiController
  AUTH_TYPES = {
    'LOGIN_WITH_PASSWORD' => 'password_auth',
    'LOGIN_WITH_OTP' => 'otp_auth',
    'LOGIN_WITH_GOOGLE' => 'google_auth'
  }
  OTP_ACTION_TYPES = %w(create verify)
  OTP_PARAMS = %w(email mobile)

  skip_before_action :authenticate_request, except: [:logout, :verify_email]
  skip_before_action :check_origin, only: [:verify_reset_link, :email_verification]
  before_action :verify_auth_type, except: [:logout, :verify_reset_link, :verify_email, :email_verification]
  before_action :is_login_with_password, only: [:forgot_password, :reset_password]
  before_action :load_user_from_token, only: [:verify_reset_link, :reset_password, :email_verification]

  def send_otp
    if @auth_type != 'LOGIN_WITH_OTP'
      render status: 400, json: { success: false, message: I18n.t('validation.invalid', param: 'Authentication type') }
      return
    end

    if params['action_type'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Action type') }
      return
    elsif !OTP_ACTION_TYPES.include?(params['action_type'])
      render status: 400, json: { success: false, message: I18n.t('validation.invalid', param: 'Action type') }
      return
    end

    keys_present = OTP_PARAMS.select { |key| params[key].present? }
    if keys_present.length != 1
      render status: 400, json: { success: false, message: I18n.t('auth.required', param: OTP_PARAMS.join(', ')) }
      return
    end

    user = User.new(keys_present[0] => params[keys_present[0]])
    user.send("validate_#{keys_present[0]}", params['action_type'])
    if user.errors.any?
      render status: 400, json: { success: false, message: I18n.t('user.otp_failed'), reason: user.errors.messages }
      return
    end
    
    render status: 200, json: { success: true, message: I18n.t('user.otp_success'), data: { token: user.send_otp(keys_present[0], params[keys_present[0]]) } }
  end

  def logout
    session = User.current.user_sessions.find_by_token(UserSession.extract_token(request.headers['Authorization']))
    if session.present? && session.destroy
      render status: 200, json: { success: true, message: I18n.t('auth.logout_success') }
      return
    end

    render status: 200, json: { success: false, message: I18n.t('auth.logout_failed') }
  end

  def forgot_password
    if params['email'].blank?
      render status: 400, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { email: [ I18n.t('validation.required', param: 'Email') ] } }
      return
    end

    user = User.find_by_email(params['email'])
    if user.nil?
      render status: 404, json: { success: false, message: I18n.t('auth.reset_password.trigger_failed'), reason: { email: [ I18n.t('user.not_found') ] } }
      return
    end

    user.trigger_password_reset
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.trigger_success') }
  end

  def verify_reset_link
    render status: 200, json: { success: true, message: 'success' }
  end

  def reset_password
    if params['password'].blank?
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('validation.required', param: 'Password') ] } }
      return
    elsif params['password'].to_s.length < User::PASSWORD_MIN_LENGTH
      render status: 400, json: { success: false, message: I18n.t('user.create_failed'), reason: { password: [ I18n.t('user.password.invalid', length: User::PASSWORD_MIN_LENGTH) ] } }
      return
    end

    @user.update!(password: params['password'])
    Core::Redis.delete(Core::Redis::RESET_PASSWORD % { token: params['token'] })
    render status: 200, json: { success: true, message: I18n.t('auth.reset_password.reset_success') }
  end

  def verify_email
    if User.current.verified
      render status: 400, json: { success: false, message: I18n.t('user.already_verified') }
      return
    end

    User.current.trigger_email_verification
    render status: 200, json: { success: true, message: I18n.t('auth.verify_email.trigger_success') }
  end

  def email_verification
    if @user.verified
      render status: 400, json: { success: false, message: I18n.t('user.already_verified') }
    else
      @user.update!(verified: true)
      render status: 200, json: { success: true, message: I18n.t('auth.verify_email.verify_success') }
    end
    Core::Redis.delete(Core::Redis::VERIFY_EMAIL % { token: params['token'] })
  end

  private
  def verify_auth_type
    auth_types = Core::Configuration.get(CoreConfig['auth']['methods'])
    if auth_types.class == String
      @auth_type = auth_types
    elsif params['auth_type'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Authentication type') }
      return
    elsif !AUTH_TYPES.key?(params['auth_type'])
      render status: 400, json: { success: false, message: I18n.t('validation.invalid', param: 'Authentication type') }
      return
    elsif !auth_types.include? params['auth_type']
      render status: 400, json: { success: false, message: I18n.t('validation.unconfigured', param: 'Authentication type') }
      return
    else
      @auth_type = params['auth_type']
    end
  end

  def load_user_from_token
    if params['token'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.required', param: 'Verification token') }
      return
    end

    user_id = if params['action'] == 'email_verification'
      Core::Redis.fetch(Core::Redis::VERIFY_EMAIL % { token: params['token'] }) { nil }
    else
      Core::Redis.fetch(Core::Redis::RESET_PASSWORD % { token: params['token'] }) { nil }
    end
    
    if user_id.blank?
      render status: 400, json: { success: false, message: I18n.t('user.param_expired', param: 'Verification link') }
      return
    end

    @user = User.find_by_id(user_id)
    if @user.nil?
      render status: 400, json: { success: false, message: I18n.t('user.not_found') }
      return
    end
  end

  def is_login_with_password
    if @auth_type != 'LOGIN_WITH_PASSWORD'
      render status: 400, json: { success: false, message: I18n.t('validation.invalid', param: 'Authentication type') }
      return
    end
  end
end
