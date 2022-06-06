class AuthMailer < ApplicationMailer
  def email_otp options = {}
    @code = options['code']
    mail(to: options['value'], subject: 'Zinger - OTP Verification')
  end
end
