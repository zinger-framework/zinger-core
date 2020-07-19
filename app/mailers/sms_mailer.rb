class SmsMailer

  def self.mobile_verification options = {}
    mobile = options[:to]
    otp = options[:code]
    # TODO: Write code to send OTP via SMS
    Rails.logger.debug "==== OTP:#{otp} sent to #{mobile} ===="
  end
end
