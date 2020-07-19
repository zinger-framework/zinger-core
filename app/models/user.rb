class User < ApplicationRecord
  has_secure_password(validations: false)
  has_one_time_password

  attr_accessor :type, :otp
  has_many :user_sessions

  EMAIL_REGEX = /\S+@\S+\.[a-z]+/i
  MOBILE_REGEX = /^[0-9]{10}$/
  USER_NAME_REGEX = /^[a-z0-9]{5,}$/i
  OTP_REGEX = /^[0-9]{6}$/i
  PASSWORD_MIN_LENGTH = 6
  OTP_LENGTH = 6
  AUTH_TYPE = {'EMAIL_PASSWORD' => 1, 'USERNAME_PASSWORD' => 2, 'MOBILE_OTP' => 3, 'GOOGLE_AUTH' => 4}

  validate :create_validations, on: :create

  def create_validations
    if [1, 4].include?(type)
      is_valid_email = valid_email
      return errors.add(:email, is_valid_email) if is_valid_email.class == String
    end
    if type == 2
      is_valid_user_name = valid_user_name
      return errors.add(:user_name, is_valid_user_name) if is_valid_user_name.class == String
    end
    if [1, 2].include?(type)
      is_valid_password = valid_password
      return errors.add(:password, is_valid_password) if is_valid_password.class == String
    end
    if type == 3
      is_valid_mobile = valid_mobile
      return errors.add(:mobile, is_valid_mobile) if is_valid_mobile.class == String
      is_valid_otp = valid_otp
      return errors.add(:otp, is_valid_otp) if is_valid_otp.class == String
    end
  end

  def valid_email
    self.email = email.to_s.strip.downcase
    return I18n.t('user.validation.required', param: 'Email address') if email.blank?
    return I18n.t('user.validation.invalid', param: 'Email address') unless email.match(EMAIL_REGEX)
    return I18n.t('user.validation.already_taken', param: email) if User.where(email: email).count > 0
    return true
  end

  def valid_mobile
    self.mobile = mobile.to_s.strip
    return I18n.t('user.validation.required', param: 'Mobile number') if mobile.blank?
    return I18n.t('user.validation.invalid', param: 'Mobile number') unless mobile.match(MOBILE_REGEX)
    return I18n.t('user.validation.already_taken', param: mobile) if User.where(mobile: mobile).count > 0
    return true
  end

  private

  def valid_password
    return I18n.t('user.validation.required', param: 'Password') if password.blank?
    return I18n.t('user.validation.password.invalid') if password.length < PASSWORD_MIN_LENGTH
    return true
  end

  def valid_otp
    self.otp = otp.to_s.strip
    return I18n.t('user.validation.required', param: 'OTP') if otp.blank?
    return I18n.t('user.validation.invalid', param: 'OTP') unless otp.match(OTP_REGEX)
    return true
  end

  def valid_user_name
    self.user_name = user_name.to_s.strip.downcase
    return I18n.t('user.validation.required', param: 'User name') if user_name.blank?
    return I18n.t('user.validation.invalid', param: 'User name') unless user_name.match(USER_NAME_REGEX)
    return I18n.t('user.validation.already_taken', param: user_name) if User.where(user_name: user_name).count > 0
    return true
  end
end
