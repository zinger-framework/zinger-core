class Core::Ratelimit
  CONFIGS = [
    { 'path' => '/v[0-9]+/auth/otp/.*', 'methods' => %w(POST), 'per_ip' => true,
      'limit' => 5, 'window' => 1800, 'message' => 'exceeded' },
    { 'path' => '/v[0-9]+/auth/signup/.*', 'methods' => %w(POST), 'per_ip' => true,
      'limit' => 5, 'window' => 600, 'message' => 'registration' },
    { 'path' => '/v[0-9]+/auth/login/.*', 'methods' => %w(POST), 'params' => %w(email mobile),
      'limit' => 5, 'window' => 600, 'message' => 'login' },
    { 'path' => '/v[0-9]+/auth/reset_password', 'methods' => %w(POST), 'per_ip' => true,
      'limit' => 5, 'window' => 600, 'message' => 'reset_password' }
  ]
  
  def self.reached? request
    config = Core::Ratelimit::CONFIGS.select do |config|
      config['methods'].include?(request.method) && request.path.match("^#{config['path']}$").present?
    end.first

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
    key = "RT_LMT:#{request.method}:#{request.path}"

    key = "#{key}:#{request.ip}" if config['per_ip']
    key = "#{key}:#{Customer.current.id}" if config['per_customer'] && Customer.current.present?
    config['params'].each { |param| key = "#{key}:#{request.params[param]}" } if config['params'].present?

    return key
  end
end
