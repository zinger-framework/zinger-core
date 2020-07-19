SidekiqConfig = YAML.load_file(File.join(Rails.root, 'config', 'sidekiq_config.yml'))
sidekiq_url = "redis://#{SidekiqConfig['host']}:#{SidekiqConfig['port']}"

Sidekiq.configure_server do |config|
  ActiveRecord::Base.logger = Logger.new("#{Rails.root}/log/#{ENV['SIDEKIQ_LOG']}.log") if ENV.key? 'SIDEKIQ_LOG'
  config.redis = { :url => sidekiq_url, :namespace => SidekiqConfig['namespace'] }
end

Sidekiq.configure_client do |config|
  config.redis = { :url => sidekiq_url, :namespace => SidekiqConfig['namespace'] }
end

Sidekiq.default_worker_options = { retry: 0, backtrace: 20 }

require 'sidekiq'
require 'sidekiq/web'

SidekiqSettings = YAML.load_file(File.join(Rails.root, 'config', 'sidekiq.yml'))
Sidekiq::Web.use(Rack::Auth::Basic) do |user, password|
  [user, password] == [SidekiqSettings['username'], SidekiqSettings['password']]
end
