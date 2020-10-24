AppConfig = YAML.load_file(File.join(Rails.root, 'config', 'app_config.yml'))
SmtpConfig = YAML.load_file(File.join(Rails.root, 'config', 'smtp_config.yml'))
PlatformConfig = YAML.load_file(File.join(Rails.root, 'config', 'platform_config.yml'))
