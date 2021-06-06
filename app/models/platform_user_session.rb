class PlatformUserSession < ApplicationRecord
  belongs_to :platform_user

  before_create :set_token
  after_create :clear_cache
  after_destroy_commit :clear_cache

  def get_jwt_token two_fa
    return JWT.encode({ 'platform_user_id' => self.platform_user_id, 'expiry_time' => Time.now.next_day.to_i, 'token' => self.token, 
      'two_fa' => two_fa }, AppConfig['platform_auth'])
  end

  def self.decode_jwt_token jwt_token
    begin
      return JWT.decode(jwt_token, AppConfig['platform_auth'])[0].to_h
    rescue => e
      return {}
    end
  end

  def self.extract_token jwt_token
    return PlatformUserSession.decode_jwt_token(jwt_token)['token']
  end

  def self.cache_key platform_user_id
    Core::Redis::PLATFORM_USER_SESSIONS_BY_ID % { id: platform_user_id }
  end

  def self.fetch_platform_user jwt_token
    payload = PlatformUserSession.decode_jwt_token(jwt_token)
    return nil if payload.blank? || Time.now.to_i > payload['expiry_time']

    sessions = Core::Redis.fetch(PlatformUserSession.cache_key(payload['platform_user_id']), { type: Array }) do
      PlatformUserSession.where(platform_user_id: payload['platform_user_id']).map(&:token)
    end
    platform_user = sessions.include?(payload['token']) ? PlatformUser.fetch_by_id(payload['platform_user_id']) : nil
    return platform_user, payload
  end

  private

  def set_token
    self.token = Base64.encode64("#{self.platform_user_id}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
  end

  def clear_cache
    Core::Redis.delete(PlatformUserSession.cache_key(self.platform_user_id))
  end
end
