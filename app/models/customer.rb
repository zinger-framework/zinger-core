class Customer < ApplicationRecord
  PASSWORD_MIN_LENGTH = PlatformConfig['password_min_length']
  OTP_LENGTH = PlatformConfig['otp_length']
  EMAIL_REGEX = /\S+@\S+\.[a-z]+/i
  MOBILE_REGEX = /^[0-9]{10}$/
  STATUSES = { 'ACTIVE' => 1, 'BLOCKED' => 2 }

  has_secure_password(validations: false)
  default_scope { where(deleted: false) }
  
  validate :create_validations, on: :create
  after_commit :clear_cache
  after_update :clear_sessions

  has_many :customer_sessions

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::CUSTOMER_BY_ID % { id: id }, { type: Customer }) { Customer.find_by_id(id) }
  end

  def is_blocked?
    self.status != STATUSES['ACTIVE']
  end

  def make_current
    Thread.current[:customer] = self
  end

  def self.reset_current
    Thread.current[:customer] = nil
  end

  def self.current
    Thread.current[:customer]
  end

  def self.send_otp options
    case options[:action]
    when 'signup'
      return I18n.t('validation.already_taken', param: options[:value]) if self.exists?(options[:param] => options[:value])
    when 'login'
    when 'reset_password'
      customer = self.where(options[:param] => options[:value]).first
      return I18n.t('customer.not_found') if customer.blank?
      return I18n.t('customer.account_blocked', platform: PlatformConfig['name']) if customer.is_blocked?
    end

    token = Base64.encode64("#{options[:value]}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    options.merge!({ code: self.otp, token: token }).except!(:action)
    MailerWorker.perform_async(options.to_json)

    return { token: token }
  end

  private

  def validate_email action = nil
    self.email = self.email.to_s.strip.downcase
    return errors.add(:email, I18n.t('validation.invalid', param: 'Email address')) unless self.email.match(EMAIL_REGEX)
    return errors.add(:email, I18n.t('validation.already_taken', param: self.email)) if self.exists?(email: self.email)
  end

  def validate_mobile action = nil
    self.mobile = self.mobile.to_s.strip
    return errors.add(:mobile, I18n.t('validation.invalid', param: 'Mobile number')) unless self.mobile.match(MOBILE_REGEX)
    return errors.add(:mobile, I18n.t('validation.already_taken', param: self.mobile)) if self.exists?(mobile: self.mobile)
  end

  def create_validations
    validate_email('create') if self.email.present?
    validate_mobile('create') if self.mobile.present?
  end

  def self.otp
    rand(10**(OTP_LENGTH - 1)..10**OTP_LENGTH - 1).to_s
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::CUSTOMER_BY_ID % { id: self.id })
  end

  def clear_sessions
    if self.saved_change_to_password_digest?
      CustomerSession.where(customer_id: self.id).delete_all
      Core::Redis.delete(CustomerSession.cache_key(self.id))
    end
  end
end
