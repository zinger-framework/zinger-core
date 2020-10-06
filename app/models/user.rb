class User < ApplicationRecord
  PASSWORD_MIN_LENGTH = 6
  OTP_LENGTH = 6
  EMAIL_REGEX = /\S+@\S+\.[a-z]+/i
  MOBILE_REGEX = /^[0-9]{10}$/
  USER_NAME_REGEX = /^[a-z0-9]{5,}$/i

  has_secure_password(validations: false)
  has_one_time_password length: OTP_LENGTH
  validate :create_validations, on: :create
  after_commit :clear_cache
  after_update :clear_sessions

  has_many :user_sessions

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::USER_BY_ID % { id: id }, { type: User }) { User.find_by_id(id) }
  end

  def trigger_password_reset
    MailerWorker.perform_async('reset_password', { email: self.email, user_id: self.id })
  end

  def trigger_email_verification
    MailerWorker.perform_async('verify_email', { email: self.email, user_id: self.id })
  end

  def validate_email action
    self.email = self.email.to_s.strip.downcase
    errors.add(:email, I18n.t('validation.invalid', param: 'Email address')) unless self.email.match(EMAIL_REGEX)

    if action == 'create'
      errors.add(:email, I18n.t('validation.already_taken', param: self.email)) if User.exists?(email: self.email)
    elsif action == 'verify'
      errors.add(:email, I18n.t('user.not_found')) unless User.exists?(email: self.email)
    end
  end

  def validate_mobile action
    self.mobile = self.mobile.to_s.strip
    errors.add(:mobile, I18n.t('validation.invalid', param: 'Mobile number')) unless self.mobile.match(MOBILE_REGEX)
    
    if action == 'create'
      errors.add(:mobile, I18n.t('validation.already_taken', param: self.mobile)) if User.exists?(mobile: self.mobile) 
    elsif action == 'verify'
      errors.add(:mobile, I18n.t('user.not_found')) unless User.exists?(mobile: self.mobile)   
    end
  end

  def make_current
    Thread.current[:user] = self
  end

  def self.reset_current
    Thread.current[:user] = nil
  end

  def self.current
    Thread.current[:user]
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
    token = Base64.encode64("#{value}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    Core::Redis.setex(Core::Redis::OTP_VERIFICATION % { token: token }, { key => value, 'code' => code }, 5.minutes.to_i)
    MailerWorker.perform_async('verify_otp', { to: value, code: code, mode: key })
    return token
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::USER_BY_ID % { id: self.id })
  end

  def clear_sessions
    if self.saved_change_to_password_digest?
      UserSession.where(user_id: self.id).delete_all
      Core::Redis.delete(UserSession.cache_key(self.id))
    end
  end
end
