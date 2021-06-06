class AdminUserSession < ApplicationRecord
  belongs_to :admin_user

  before_create :set_token
  after_create :clear_cache
  after_destroy_commit :clear_cache

  def get_jwt_token two_fa
    return JWT.encode({ 'admin_user_id' => self.admin_user_id, 'expiry_time' => Time.now.next_day.to_i, 'token' => self.token, 
      'two_fa' => two_fa }, AppConfig['admin_auth'])
  end

  def self.decode_jwt_token jwt_token
    begin
      return JWT.decode(jwt_token, AppConfig['admin_auth'])[0].to_h
    rescue => e
      return {}
    end
  end

  def self.extract_token jwt_token
    return AdminUserSession.decode_jwt_token(jwt_token)['token']
  end

  def self.cache_key admin_user_id
    Core::Redis::ADMIN_USER_SESSIONS_BY_ID % { id: admin_user_id }
  end

  def self.fetch_admin_user jwt_token
    payload = AdminUserSession.decode_jwt_token(jwt_token)
    return nil if payload.blank? || Time.now.to_i > payload['expiry_time']

    sessions = Core::Redis.fetch(AdminUserSession.cache_key(payload['admin_user_id']), { type: Array }) do
      AdminUserSession.where(admin_user_id: payload['admin_user_id']).map(&:token)
    end
    admin_user = sessions.include?(payload['token']) ? AdminUser.fetch_by_id(payload['admin_user_id']) : nil
    return admin_user, payload
  end

  private

  def set_token
    self.token = Base64.encode64("#{self.admin_user_id}-#{Time.now.to_i}-#{rand(1000..9999)}").strip.gsub('=', '')
  end

  def clear_cache
    Core::Redis.delete(AdminUserSession.cache_key(self.admin_user_id))
  end
end
