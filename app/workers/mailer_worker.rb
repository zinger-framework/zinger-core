class MailerWorker
  include Sidekiq::Worker

  def perform(mail, args = {})
    send(mail, args)
  end

  def reset_password args
    # token = HyptoPartner::Redis.fetch(HyptoPartner::Redis::RESET_PASSWORD % { token: args['email'] }, { type: String, expiry: 15.minutes.to_i }) do
    #   Base64.encode64("#{rand(1000)}-#{args['email']}-#{Time.now.to_i}").strip.gsub('=', '')
    # end
    # HyptoPartner::Redis.setex(HyptoPartner::Redis::RESET_PASSWORD % { token: token }, args['user_id'], 15.minutes.to_i)
    #
    # link = Rails.application.routes.url_helpers.reset_password_ui_url(host: AppConfig['ui_endpoint'], token: token)
    # UserMailer.reset_password({ to: args['email'], link: link }).deliver!
  end

  def email_verification email
    token = Base64.encode64("#{rand(1000..9999)}-#{email}-#{Time.now.to_i}").strip.gsub(/[^A-z\d]/, '')
    Core::Redis.setex(Core::Redis::VERIFICATION % { token: token }, email, 5.minutes.to_i)

    link = Rails.application.routes.url_helpers.verify_signup_link_v2_auth_signup_index_url(host: AppConfig['endpoint'], token: token)
    UserMailer.email_verification({ to: email, link: link }).deliver!
  end

  def mobile_verification args
    token = Base64.encode64("#{rand(1000..9999)}-#{args['code']}-#{Time.now.to_i}").strip
    Core::Redis.setex(Core::Redis::VERIFICATION % { token: args['mobile'] }, token, 5.minutes.to_i)

    SmsMailer.mobile_verification({ to: args['mobile'], code: args['code'] })
  end
end
