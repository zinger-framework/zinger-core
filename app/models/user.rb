class User < ApplicationRecord
  PASSWORD_MIN_LENGTH = 6
  OTP_LENGTH = 6
  EMAIL_REGEX = /\S+@\S+\.[a-z]+/i
  MOBILE_REGEX = /^[0-9]{10}$/
  USER_NAME_REGEX = /^[a-z0-9]{5,}$/i

  has_secure_password(validations: false)
  has_one_time_password length: OTP_LENGTH
  validate :create_validations, on: :create

  has_many :user_sessions

  def validate_email action
    self.email = email.to_s.strip.downcase
    errors.add(:email, I18n.t('validation.invalid', param: 'Email address')) unless email.match(EMAIL_REGEX)

    if action == 'create'
      errors.add(:email, I18n.t('validation.already_taken', param: self.email)) if User.exists?(email: self.email)
    elsif action == 'verify'
      errors.add(:email, I18n.t('user.not_found')) unless User.exists?(email: self.email)
    end
  end

  def validate_mobile
    self.mobile = mobile.to_s.strip
    errors.add(:mobile, I18n.t('validation.invalid', param: 'Mobile number')) unless mobile.match(MOBILE_REGEX)
    
    if action == 'create'
      errors.add(:mobile, I18n.t('validation.already_taken', param: self.mobile)) if User.exists?(mobile: self.mobile) 
    elsif action == 'verify'
      errors.add(:mobile, I18n.t('user.not_found')) unless User.exists?(mobile: self.mobile)   
    end
  end

  def create_validations
    validate_email('create') if self.email.present?
    validate_mobile('create') if self.mobile.present?

    if self.user_name.present?
      self.user_name = user_name.to_s.strip.downcase
      errors.add(:user_name, I18n.t('validation.invalid', param: 'User name')) unless user_name.match(USER_NAME_REGEX)
      errors.add(:user_name, I18n.t('validation.already_taken', param: self.user_name)) if User.exists?(user_name: self.user_name)
    end
  end

  def send_otp key, value
    self.otp_regenerate_secret
    code = self.otp_code(time: Time.now)
    token = Base64.encode64("#{rand(1000)}-#{value}-#{Time.now.to_i}").strip.gsub('=', '')
    Core::Redis.setex(Core::Redis::OTP_VERIFICATION % { token: token }, { key => value, 'code' => code }, 5.minutes.to_i)
    MailerWorker.perform_async("#{key}_verification", { to: value, code: code })
    return token
  end
end
