class MailerWorker
  include Sidekiq::Worker

  def perform(params = '{}')
    options = JSON.parse(params)

    case options['param']
    when 'mobile'
      SmsMailer.send_otp(options.slice(*['value', 'code']))
    when 'email'
      UserMailer.send_otp(options.slice(*['value', 'code'])).deliver!
    end

    Core::Redis.setex(Core::Redis::OTP_VERIFICATION % { token: options['token'] }, options, 5.minutes.to_i)
    Rails.logger.debug "==== OTP:#{options['code']} sent to #{options['value']} ===="
  end
end
