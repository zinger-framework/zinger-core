class MailerWorker
  include Sidekiq::Worker

  def perform(purpose, params = '{}')
    options = JSON.parse(params)

    case purpose
    when 'send_otp'
      case options['param']
      when 'mobile'
        MailerWorker.sms_otp(options.slice(*['value', 'code']))
      when 'email'
        AuthMailer.email_otp(options.slice(*['value', 'code'])).deliver!
      end
      Core::Redis.setex(Core::Redis::OTP_VERIFICATION % { token: options['token'] }, options, 5.minutes.to_i)
      Rails.logger.debug "==== OTP:#{options['code']} sent to #{options['value']} ===="
      
    when 'platform'
      PlatformMailer.notify(options['subject'], options.except('subject')).deliver!
    end
  end

  def self.sms_otp options = {}
    mobile = options['value']
    otp_code = options['code']
    # TODO: Integrate with any SMS gateways
  end
end
