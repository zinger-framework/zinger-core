module Core
  class Configuration
    def self.get key
      key['selected'] || key['default']
    end
  end
end
