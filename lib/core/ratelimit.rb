class Core::Ratelimit
  CONFIGS = [
    { 'pattern' => 'api/auth/otp#signup', 'per_ip' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'api/auth/otp#login', 'per_ip' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'api/auth/otp#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'api/auth/otp#reset_profile', 'per_customer' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'api/auth/signup#password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'api/auth/signup#otp', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'api/auth/signup#google', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'api/auth/login#password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'api/auth/login#otp', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'api/auth/login#google', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'api/auth#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },
    { 'pattern' => 'api/customer#reset_profile', 'per_customer' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_profile' },
    { 'pattern' => 'api/customer#password', 'per_customer' => true, 'limit' => 5, 'window' => 600, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth/otp#login', 'per_employee' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth/otp#forgot_password', 'per_ip' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth/otp#verify_mobile', 'per_employee' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth/otp#signup', 'per_ip' => true, 'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth#login', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'pattern' => 'admin/auth#verify_otp', 'per_employee' => true, 'limit' => 5, 'window' => 600, 'message' => 'exceeded' },
    { 'pattern' => 'admin/auth#reset_password', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' },
    { 'pattern' => 'admin/auth#signup', 'per_ip' => true, 'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'pattern' => 'admin/user_profile#reset_password', 'per_employee' => true, 'limit' => 5, 'window' => 600, 'message' => 'reset_password' }
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
    key = "#{key}:#{Employee.current.id}" if config['per_employee'] && Employee.current.present?
    config['params'].each { |param| key = "#{key}:#{request.params[param]}" } if config['params'].present?
    return key
  end
end
