Rails.application.config.action_mailer.smtp_settings = {
    address: SmtpConfig['address'],
    port: SmtpConfig['port'],
    user_name: SmtpConfig['user_name'],
    password: SmtpConfig['password'],
    authentication: :login
}
