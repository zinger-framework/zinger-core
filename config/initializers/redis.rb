RedisConfig = YAML.load_file(File.join(Rails.root, 'config', 'redis.yml'))

$redis = Redis::Namespace.new(RedisConfig['namespace'], redis: Redis.new(host: RedisConfig['host'], port: RedisConfig['port']))
