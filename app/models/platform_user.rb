class PlatformUser < ApplicationRecord
  STATUSES = { 'PENDING' => 1, 'ACTIVE' => 2, 'BLOCKED' => 3 }
  TWO_FA_STATUSES = { 'NOT_APPLICABLE' => 1, 'UNVERIFIED' => 2, 'VERIFIED' => 3 }

  has_secure_password(validations: false)
  default_scope { where(deleted: false) }

  validates :password, 
    presence: { message: I18n.t('validation.required', param: 'Password') }, 
    length: { minimum: PASSWORD_MIN_LENGTH, message: I18n.t('validation.password.invalid', length: PASSWORD_MIN_LENGTH) }, 
    confirmation: { message: I18n.t('validation.password.mismatch') }, 
    if: :validate_password?
  validates :password_confirmation, 
    presence: { message: I18n.t('validation.required', param: 'Confirm password') }, 
    if: :validate_password?

  validate :create_validations, on: :create
  validate :update_validations, on: :update
  after_commit :clear_cache
  after_update :clear_sessions

  has_many :platform_user_sessions

  def as_json purpose = nil
    case purpose
    when 'ui_profile'
      return { 'name' => self.name, 'email' => self.email, 'mobile' => self.mobile, 'two_fa_enabled' => self.two_fa_enabled }
    end
  end

  def self.send_otp options
    token = Base64.encode64("#{options[:value]}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    options = options.merge({ code: PlatformUser.otp, token: token })
    MailerWorker.perform_async('send_otp', options.to_json)
    return token
  end

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::PLATFORM_USER_BY_ID % { id: id }, { type: PlatformUser }) { PlatformUser.find_by_id(id) }
  end

  def is_blocked?
    self.status == STATUSES['BLOCKED']
  end

  def make_current
    Thread.current[:platform_user] = self
  end

  def self.reset_current
    Thread.current[:platform_user] = nil
  end

  def self.current
    Thread.current[:platform_user]
  end

  private

  def self.otp
    rand(10**(OTP_LENGTH - 1)..10**OTP_LENGTH - 1).to_s
  end

  def validate_password?
    self.new_record? || self.password_digest_changed?
  end

  def create_validations
    if self.email.blank?
      errors.add(:name, I18n.t('validation.required', param: 'Email address'))
    else
      self.email = self.email.to_s.strip.downcase
      errors.add(:email, I18n.t('validation.invalid', param: 'email address')) unless self.email.match(EMAIL_REGEX)
      errors.add(:email, I18n.t('auth.already_exist', key: :email, value: self.email)) if PlatformUser.exists?(email: self.email)
    end

    if self.name.blank?
      errors.add(:name, I18n.t('validation.required', param: 'Name'))
    else
      errors.add(:name, I18n.t('validation.name_long')) if self.name.length > 100
      errors.add(:name, I18n.t('validation.invalid', param: 'name')) unless self.name.match(NAME_REGEX)
    end
  end

  def update_validations
    errors.add(:mobile, I18n.t('validation.required', param: 'Mobile number')) if self.two_fa_enabled_changed? && 
      self.two_fa_enabled && self.mobile.blank?
    
    if self.mobile_changed?
      if self.mobile.blank?
        errors.add(:mobile, I18n.t('validation.required', param: 'Mobile number'))
      else
        self.mobile = self.mobile.to_s.strip
        errors.add(:mobile, I18n.t('validation.invalid', param: 'mobile number')) unless self.mobile.match(MOBILE_REGEX)
      end
    end

    if self.name_changed?
      if self.name.blank?
        errors.add(:name, I18n.t('validation.required', param: 'Name'))
      else
        errors.add(:name, I18n.t('validation.name_long')) if self.name.length > 100
        errors.add(:name, I18n.t('validation.invalid', param: 'name')) unless self.name.match(NAME_REGEX)
      end
    end
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::PLATFORM_USER_BY_ID % { id: self.id })
  end

  def clear_sessions
    if self.saved_change_to_password_digest? || (self.saved_change_to_two_fa_enabled? && self.two_fa_enabled)
      PlatformUserSession.where(platform_user_id: self.id).delete_all
      Core::Redis.delete(PlatformUserSession.cache_key(self.id))
    end
  end
end
