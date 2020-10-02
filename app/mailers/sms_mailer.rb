class SmsMailer
  def self.mobile_verification options = {}
    mobile = options['to']
    otp_code = options['code']
    # TODO: Integrate with any SMS gateways
  end
end
