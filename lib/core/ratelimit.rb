class Core::Ratelimit
  CONFIGS = [
    { 'pattern' => 'api/auth#otp', 'per_ip' => true, 'params' => %w(purpose), 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'api/auth#signup', 'per_ip' => true, 'params' => %w(purpose), 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'api/auth#login', 'per_ip' => true, 'params' => %w(purpose), 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'api/auth#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },
    { 'pattern' => 'api/user_profile#reset_profile', 'per_customer' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_profile' },
    { 'pattern' => 'api/user_profile#reset_password', 'per_customer' => true, 'limit' => 5, 'window' => 600, 'message' => 'exceeded' },
    
    { 'pattern' => 'admin/auth#otp', 'per_ip' => true, 'params' => %w(purpose), 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth#login', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'admin/auth#verify_otp', 'per_admin_user' => true, 'limit' => 5, 'window' => 600, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },
    { 'pattern' => 'admin/auth#signup', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'admin/user_profile#reset_password', 'per_admin_user' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },

    { 'pattern' => 'platform/auth#otp', 'per_ip' => true, 'params' => %w(purpose), 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'platform/auth#login', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'platform/auth#verify_otp', 'per_platform_user' => true, 'limit' => 5, 'window' => 600, 'message' => 'exceeded' },
    { 'pattern' => 'platform/auth#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },
    { 'pattern' => 'platform/user_profile#reset_password', 'per_platform_user' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' }
  ]
  
  def self.reached? request
    config = CONFIGS.find { |config| "#{request.params['controller']}##{request.params['action']}" == config['pattern'] }
    return false if config.blank?

    key = Core::Ratelimit.key(request, config)
    value, time_now = Core::Redis.get(key), Time.now.to_i
    value = "0:#{time_now + config['window']}:#{config['limit']}" if value.nil?
    count, expiry, limit = value.split(':').map(&:to_i)
    count += 1

    Core::Redis.setex(key, "#{count}:#{expiry}:#{limit}", expiry - time_now)
    return (count > limit) ? I18n.t("ratelimit.#{config['message']}") : false
  end

  def self.key request, config
    key = "RT_LMT:#{request.params['controller']}##{request.params['action']}"
    key = "#{key}:#{request.ip}" if config['per_ip']
    key = "#{key}:#{Customer.current.id}" if config['per_customer'] && Customer.current.present?
    key = "#{key}:#{AdminUser.current.id}" if config['per_admin_user'] && AdminUser.current.present?
    key = "#{key}:#{PlatformUser.current.id}" if config['per_platform_user'] && PlatformUser.current.present?
    config['params'].each { |param| key = "#{key}:#{request.params[param]}" } if config['params'].present?
    return key
  end
end
