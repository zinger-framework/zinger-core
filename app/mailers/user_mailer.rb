class UserMailer < ApplicationMailer
  def send_otp options = {}
    @code = options['code']
    mail(to: options['value'], subject: 'Zinger - OTP Verification')
  end
end
