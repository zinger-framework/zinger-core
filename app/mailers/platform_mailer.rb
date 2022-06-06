class PlatformMailer < ApplicationMailer
  TO_ADDRS = 'admin@zinger.pw'

  def notify subject, payload = {}
    @payload = payload.except('attachments')
    payload['attachments'].to_h.each { |file_name, file_path| attachments[file_name] = File.read(file_path) }
    mail(to: TO_ADDRS, subject: "Zinger - #{subject}")
  end
end
