class CustomerSession < ApplicationRecord
  AUTH_MODE = { 'PASSWORD_AUTH' => 1, 'OTP_AUTH' => 2, 'GOOGLE_AUTH' => 3 }
  belongs_to :customer

  before_create :set_token
  after_create :clear_cache
  after_destroy_commit :clear_cache

  def as_json purpose = nil, options = {}
    resp = extract_device_info
    case purpose
    when 'ui_profile'
      return { 'token' => self.token, 'login_ip' => self.login_ip, 'login_time' => self.created_at.strftime('%Y-%m-%d %H:%M:%S'), 
        'device_os' => resp['device_os'], 'browser' => resp['browser'], 'current_session' => self.token == options['token'].to_s }
    end
  end

  def get_jwt_token
    return JWT.encode({ 'customer_id' => self.customer_id, 'iat' => Time.now.to_i, 'token' => self.token }, AppConfig['api_auth'])
  end

  def self.decode_jwt_token jwt_token
    begin
      return JWT.decode(jwt_token, AppConfig['api_auth'])[0].to_h
    rescue => e
      return {}
    end
  end

  def self.extract_token jwt_token
    return CustomerSession.decode_jwt_token(jwt_token)['token']
  end

  def self.cache_key customer_id
    Core::Redis::CUSTOMER_SESSIONS_BY_ID % { id: customer_id }
  end

  def self.fetch_customer jwt_token
    payload = CustomerSession.decode_jwt_token(jwt_token)
    return nil if payload.blank?

    sessions = Core::Redis.fetch(CustomerSession.cache_key(payload['customer_id']), { type: Array }) do
      CustomerSession.where(customer_id: payload['customer_id']).map(&:token)
    end
    
    return sessions.include?(payload['token']) ? Customer.fetch_by_id(payload['customer_id']) : nil
  end

  private

  def set_token
    self.token = Base64.encode64("#{self.customer_id}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
  end

  def clear_cache
    Core::Redis.delete(CustomerSession.cache_key(self.customer_id))
  end

  def extract_device_info
    browser = Browser.new(self.user_agent)
    return { 'device_os' => "#{[:mac, :linux].include?(browser.platform.id) ? browser.platform.id.to_s.capitalize : browser.platform.name} \
#{browser.platform.version if browser.platform.version != '0'}".strip,
      'browser' => browser.name } if browser.known?

    return { 'device_os' => '-', 'browser' => '-' }
  end
end
