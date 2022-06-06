class ApplicationMailer < ActionMailer::Base
  default from: "Zinger-Team <#{SmtpConfig['user_name']}>"
  layout 'mailer'

  before_action :add_inline_attachment!

  def add_inline_attachment!
    attachments.inline['logo.png'] = File.read("#{Rails.root}/app/assets/images/zinger-logo.png")
  end
end
