class CustomerSession < ApplicationRecord
  belongs_to :customer

  before_create :set_token
  after_create :clear_cache
  after_destroy_commit :clear_cache

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
      self.where(customer_id: payload['customer_id']).map(&:token)
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
end
