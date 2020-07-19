class UserSession < ApplicationRecord
  belongs_to :user

  before_create :set_token

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

  private

  def set_token
    self.token = Base64.encode64("#{self.user_id}-#{Time.now.to_f.to_s.gsub(".", "-")}-#{rand(1000..9999)}").strip
  end
end
