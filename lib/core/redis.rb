module Core
  class Redis
    VERIFICATION = 'VERIFICATION:%{token}'

    def self.get key
      $redis.get(key)
    end

    def self.setex key, value, expiry
      $redis.setex(key, expiry, value)
    end

    def self.delete key
      $redis.del(key)
    end
  end
end