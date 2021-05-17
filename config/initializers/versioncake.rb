VersionCake.setup do |config|
  config.resources do |r|
    # r.resource uri_regex, obsolete, deprecated, supported
    r.resource %r{.*}, [], [], [1, 2]
  end
  config.extraction_strategy = :path_parameter
  config.rails_view_versioning = false
end
