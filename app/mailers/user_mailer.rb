class UserMailer < ApplicationMailer
  def email_verification options = {}
    @code = options['code']
    mail(to: options['to'], subject: 'Zinger - Verification')
  end
end
