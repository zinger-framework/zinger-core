class MailerWorker
  include Sidekiq::Worker

  def perform(mail, options = {})
    send(mail, options)
  end

  def email_verification options
    UserMailer.email_verification(options).deliver!
    Rails.logger.debug "==== OTP:#{options['code']} sent to #{options['to']} ===="
  end

  def mobile_verification args
    SmsMailer.mobile_verification(options)
    Rails.logger.debug "==== OTP:#{options['code']} sent to #{options['to']} ===="
  end
end
