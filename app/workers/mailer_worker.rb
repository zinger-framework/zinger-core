class MailerWorker
  include Sidekiq::Worker

  def perform(mail, options = {})
    send(mail, options)
  end

  def verify_email options
    token = Base64.encode64("#{options['email']}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    Core::Redis.setex(Core::Redis::VERIFY_EMAIL % { token: token }, options['user_id'], 15.minutes.to_i)

    link = Rails.application.routes.url_helpers.verify_email_link_url(host: AppConfig['endpoint'], token: token)
    UserMailer.verify_email({ 'to' => options['email'], 'link' => link }).deliver!
    Rails.logger.debug "==== Verification email sent to #{options['to']} ===="
  end

  def verify_otp options
    case options['mode']
    when 'mobile'
      SmsMailer.verify_otp(options)
    when 'email'
      UserMailer.verify_otp(options).deliver!
    end
    Rails.logger.debug "==== OTP:#{options['code']} sent to #{options['to']} ===="
  end

  def reset_password options
    token = Base64.encode64("#{options['email']}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
    Core::Redis.setex(Core::Redis::RESET_PASSWORD % { token: token }, options['user_id'], 15.minutes.to_i)

    link = Rails.application.routes.url_helpers.verify_reset_link_url(host: AppConfig['endpoint'], token: token)
    UserMailer.reset_password({ 'to' => options['email'], 'link' => link }).deliver!
    Rails.logger.debug "==== Verification email sent to #{options['to']} ===="
  end
end
