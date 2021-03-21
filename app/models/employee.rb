class Employee < ApplicationRecord
  STATUSES = { 'ACTIVE' => 1, 'BLOCKED' => 2, 'PENDING' => 3 }
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

  after_commit :clear_cache
  after_update :clear_sessions

  has_many :employee_sessions
  has_and_belongs_to_many :shops

  def as_json purpose = nil
    case purpose
    when 'ui_profile'
      return { 'name' => self.name, 'email' => self.email, 'mobile' => self.mobile, 'two_fa_enabled' => self.two_fa_enabled }
    end
  end

  def self.send_otp options
    token = Base64.encode64("#{options[:value]}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    options.merge!({ code: Employee.otp, token: token })
    MailerWorker.perform_async(options.to_json)
    return token
  end

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::EMPLOYEE_BY_ID % { id: id }, { type: Employee }) { Employee.find_by_id(id) }
  end

  def is_blocked?
    self.status == STATUSES['BLOCKED']
  end

  def is_pending?
    self.status == STATUSES['PENDING']
  end

  def make_current
    Thread.current[:employee] = self
  end

  def self.reset_current
    Thread.current[:employee] = nil
  end

  def self.current
    Thread.current[:employee]
  end

  private

  def self.otp
    rand(10**(OTP_LENGTH - 1)..10**OTP_LENGTH - 1).to_s
  end

  def validate_password?
    self.new_record? || self.password_digest_changed?
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::EMPLOYEE_BY_ID % { id: self.id })
  end

  def clear_sessions
    if self.saved_change_to_password_digest?
      EmployeeSession.where(employee_id: self.id).delete_all
      Core::Redis.delete(EmployeeSession.cache_key(self.id))
    end
  end
end
