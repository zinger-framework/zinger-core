class Customer < ApplicationRecord
  STATUSES = { 'ACTIVE' => 1, 'BLOCKED' => 2 }
  AUTH_MODE = { 'PASSWORD_AUTH' => 1, 'OTP_AUTH' => 2, 'GOOGLE_AUTH' => 3 }

  has_secure_password(validations: false)
  default_scope { where(deleted: false) }
  
  validate :create_validations, on: :create
  validate :update_validations, on: :update
  after_commit :clear_cache
  after_update :clear_sessions

  has_many :customer_sessions

  def as_json purpose = nil
    case purpose
    when 'ui_profile'
      return { name: self.name, email: self.email, mobile: self.mobile }
    when 'admin_profile'
      return { id: self.id, name: self.name, email: self.email, mobile: self.mobile, status: self.status, deleted: self.deleted,
      updated_at: self.updated_at.in_time_zone(PlatformConfig['time_zone']).strftime('%d-%m-%Y %H:%M') }
    end
  end

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
    return I18n.t('validation.invalid', param: 'email address') if options[:param] == 'email' && !options[:value].match(EMAIL_REGEX)
    return I18n.t('validation.invalid', param: 'mobile number') if options[:param] == 'mobile' && !options[:value].match(MOBILE_REGEX)

    case options[:action]
    when 'signup'
      return I18n.t('auth.already_exist', key: options[:param], value: options[:value]) if Customer.exists?(options[:param] => options[:value])
    when 'login'
      customer = Customer.where(options[:param] => options[:value]).first
      return I18n.t('customer.not_found') if customer.blank? || (!PlatformConfig['flexible_auth'] && 
        customer.auth_mode != Customer::AUTH_MODE['OTP_AUTH'])
      return I18n.t('customer.account_blocked', platform: PlatformConfig['name']) if customer.is_blocked?
    when 'reset_password'
      customer = Customer.where(options[:param] => options[:value]).first
      return I18n.t('customer.not_found') if customer.blank? || customer.auth_mode != Customer::AUTH_MODE['PASSWORD_AUTH']
      return I18n.t('customer.account_blocked', platform: PlatformConfig['name']) if customer.is_blocked?
    when 'reset_profile'
      return I18n.t('auth.already_exist', key: options[:param], value: options[:value]) if Customer.exists?(options[:param] => options[:value])
      options[:customer_id] = Customer.current.id
    end

    token = Base64.encode64("#{options[:value]}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    options.merge!({ code: Customer.otp, token: token }).except!(:action)
    MailerWorker.perform_async(options.to_json)

    return { token: token }
  end

  private

  def create_validations
    if self.email.present?
      self.email = self.email.to_s.strip.downcase
      return errors.add(:email, I18n.t('validation.invalid', param: 'email address')) unless self.email.match(EMAIL_REGEX)
      return errors.add(:email, I18n.t('auth.already_exist', key: :email, value: self.email)) if Customer.exists?(email: self.email)
    end

    if self.mobile.present?
      self.mobile = self.mobile.to_s.strip
      return errors.add(:mobile, I18n.t('validation.invalid', param: 'mobile number')) unless self.mobile.match(MOBILE_REGEX)
      return errors.add(:mobile, I18n.t('auth.already_exist', key: :mobile, value: self.mobile)) if Customer.exists?(mobile: self.mobile)
    end

    return errors.add(:password, I18n.t('customer.password.invalid', length: PASSWORD_MIN_LENGTH)) if self.auth_mode == AUTH_MODE['PASSWORD_AUTH'] && 
      self.password.to_s.length < PASSWORD_MIN_LENGTH
  end

  def update_validations
    if self.name_changed?
      self.name = self.name.to_s.strip
      return errors.add(:name, I18n.t('validation.required', param: 'Name')) if self.name.blank?
    end

    if self.email_changed?
      self.email = self.email.to_s.strip.downcase
      return errors.add(:email, I18n.t('validation.invalid', param: 'email address')) unless self.email.match(EMAIL_REGEX)
      return errors.add(:email, I18n.t('auth.already_exist', key: :email, value: self.email)) if Customer.exists?(email: self.email)
    end

    if self.mobile_changed?
      self.mobile = self.mobile.to_s.strip
      return errors.add(:mobile, I18n.t('validation.invalid', param: 'mobile number')) unless self.mobile.match(MOBILE_REGEX)
      return errors.add(:mobile, I18n.t('auth.already_exist', key: :mobile, value: self.mobile)) if Customer.exists?(mobile: self.mobile)
    end

    return errors.add(:password, I18n.t('customer.password.invalid', length: PASSWORD_MIN_LENGTH)) if self.password_digest_changed? && 
      self.password.to_s.length < PASSWORD_MIN_LENGTH
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
