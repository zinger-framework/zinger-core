class UserMailer < ApplicationMailer

  def reset_password options = {}
    @link = options[:link]
    mail(to: options[:to], subject: 'Zinger - Reset Password')
  end

  def email_verification options = {}
    @link = options[:link]
    mail(to: options[:to], subject: 'Zinger - Verification')
  end
end
