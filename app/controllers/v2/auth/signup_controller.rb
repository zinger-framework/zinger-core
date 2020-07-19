class V2::Auth::SignupController < ApiController
  skip_before_action :authenticate_request, :verify_partner
  before_action :load_type
  before_action :load_email_from_token, only: [:verify_link, :signup]

  def validate
    resp = if User::AUTH_TYPE[params['type']] == 3
             send_otp
           else
             send_verification_email
           end

    render status: 400, json: {success: false, message: resp} if resp.present?
  end

  def send_verification_email
    user = User.new(email: params['email'])
    resp = user.valid_email
    return resp if resp.class == String

    MailerWorker.perform_async('email_verification', user.email)
    render status: 200, json: {success: true, message: I18n.t('user.trigger_success', param: 'Verification link')}
    return
  end

  def send_otp
    user = User.new(mobile: params['mobile'], otp_secret_key: User.otp_random_secret)
    resp = user.valid_mobile
    return resp if resp.class == String

    MailerWorker.perform_async('mobile_verification', {mobile: user.mobile, code: user.otp_code})
    render status: 200, json: {success: true, message: I18n.t('user.trigger_success', param: 'Verification OTP')}
    return
  end

  def verify_link
    render status: 200, json: {success: true, message: 'success', data: {email: @email}}
  end

  def signup
    user = case @type
           when 2
             User.create(user_name: params['user_name'], password: params['password'], type: @type)
           when 3
             mobile_auth
           when 4
             User.create(email: params['email'], type: @type)
           else
             User.create(email: @email, password: params['password'], type: @type)
           end

    if user.errors.any?
      render status: 400, json: {success: false, message: user.errors.messages.values.flatten.join(', ')}
    else
      session = user.user_sessions.create(login_ip: request.ip, device_os: params['device_os'], device_app: params['device_app'])
      Core::Redis.delete(Core::Redis::VERIFICATION % {token: params['token']}) if @type == 1
      render status: 200, json: {success: true, message: I18n.t('user.create_success'), data: {token: session.get_jwt_token}}
    end
  end

  def mobile_auth
    user = User.new(mobile: params['mobile'], otp: params['otp'], type: @type)
    return user unless user.validate

    token = Core::Redis.get(Core::Redis::VERIFICATION % {token: user.mobile}) { nil }
    if token.nil?
      user.errors.add(:mobile, I18n.t('user.link_expired', param: 'OTP'))
      return user
    end

    if params['otp'] != Base64.decode64(token).split('-').second
      user.errors.add(:mobile, I18n.t('user.validation.invalid', param: 'OTP'))
      return user
    end

    user.save
    Core::Redis.delete(Core::Redis::VERIFICATION % {token: user.mobile})
    return user
  end

  private

  def load_type
    @type = User::AUTH_TYPE.fetch(params['type'], 1)
  end

  def load_email_from_token
    return if @type != 1

    if params['token'].blank?
      render status: 400, json: {success: false, message: I18n.t('user.validation.required', param: 'Token')}
      return
    end

    @email = Core::Redis.get(Core::Redis::VERIFICATION % {token: params['token']}) { nil }
    if @email.nil? || User.find_by_email(@email).present?
      render status: 400, json: {success: false, message: I18n.t('user.link_expired', param: 'Verification link')}
      return
    end
  end
end
