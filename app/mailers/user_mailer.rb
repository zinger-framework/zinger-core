class UserMailer < ApplicationMailer
  def verify_email options = {}
    @link = options['link']
    mail(to: options['to'], subject: 'Zinger - Email Verification')
  end

  def verify_otp options = {}
    @code = options['code']
    mail(to: options['to'], subject: 'Zinger - OTP Verification')
  end

  def reset_password options = {}
    @link = options['link']
    mail(to: options['to'], subject: 'Zinger - Reset Password')
  end
end
