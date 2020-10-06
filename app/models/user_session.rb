class UserSession < ApplicationRecord
  belongs_to :user

  before_create :set_token
  after_create :clear_cache
  after_destroy_commit :clear_cache

  def get_jwt_token
    return JWT.encode({ 'user_id' => self.user_id, 'iat' => Time.now.to_i, 'token' => self.token }, AppConfig['api_auth'])
  end

  def self.decode_jwt_token jwt_token
    begin
      return JWT.decode(jwt_token, AppConfig['api_auth'])[0].to_h
    rescue => e
      return {}
    end
  end

  def self.extract_token jwt_token
    return UserSession.decode_jwt_token(jwt_token)['token']
  end

  def self.cache_key user_id
    Core::Redis::USER_SESSIONS_BY_ID % { id: user_id }
  end

  def self.fetch_user jwt_token
    payload = UserSession.decode_jwt_token(jwt_token)
    return nil if payload.blank?

    sessions = Core::Redis.fetch(UserSession.cache_key(payload['user_id']), { type: Array }) do
      UserSession.where(user_id: payload['user_id']).map(&:token)
    end
    
    return sessions.include?(payload['token']) ? User.fetch_by_id(payload['user_id']) : nil
  end

  private

  def set_token
    self.token = Base64.encode64("#{self.user_id}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
  end

  def clear_cache
    Core::Redis.delete(UserSession.cache_key(self.user_id))
  end
end
