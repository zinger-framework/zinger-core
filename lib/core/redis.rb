class Core::Redis
  OTP_VERIFICATION = 'OTP_VERIFICATION:%{token}'
  ID_TOKEN_VERIFICATION = 'ID_TOKEN_VERIFICATION:%{id_token}'
  CUSTOMER_SESSIONS_BY_ID = 'CUSTOMER_SESSIONS_BY_ID:%{id}'
  EMPLOYEE_SESSIONS_BY_ID = 'EMPLOYEE_SESSIONS_BY_ID:%{id}'
  CUSTOMER_BY_ID = 'v1/CUSTOMER_BY_ID:%{id}'
  EMPLOYEE_BY_ID = 'v1/EMPLOYEE_BY_ID:%{id}'
  SHOP_BY_ID = 'v1/SHOP_BY_ID:%{id}'

  def self.marshal type, value
    if String == type
      return value
    elsif [Hash, Array].include?(type)
      return value.to_s
    else
      return Zlib::Deflate.deflate(Marshal.dump(value))
    end
  end

  def self.unmarshal type, value
    if type == String
      return value
    elsif [Hash, Array].include?(type)
      return eval(value)
    else
      return Marshal.load(Zlib::Inflate.inflate(value))
    end
  end

  def self.fetch key, options = {}, &block
    cache_data = get(key)
    if cache_data.nil?
      cache_data = marshal(options.fetch(:type, String), block.call)
      setex(key, cache_data, options.fetch(:expiry, 1.months.to_i))
    end
    unmarshal(options.fetch(:type, String), cache_data)
  end

  def self.get key
    $redis.get(key)
  end

  def self.setex key, value, expiry
    $redis.setex(key, expiry, value)
  end

  def self.delete key
    $redis.del(key)
  end

  def self.exists key
    $redis.exists(key)
  end
end
