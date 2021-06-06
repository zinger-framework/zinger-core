class ValidateParam::Shop
  DEFAULT_START_TIME = Time.find_zone(PlatformConfig['time_zone']).strptime('2020-01-01 00:00:00', '%Y-%m-%d %H:%M:%S')

  def self.load_conditions params
    conditions, start_time, end_time = {}, DEFAULT_START_TIME, Time.now.in_time_zone(PlatformConfig['time_zone'])

    if params['start_time'].present?
      begin
        start_time = Time.find_zone(PlatformConfig['time_zone']).strptime(params['start_time'], '%Y-%m-%d %H:%M:%S')
      rescue ArgumentError => e
        return I18n.t('validation.invalid_time_format')
      end
    end

    if params['end_time'].present?
      begin
        end_time = Time.find_zone(PlatformConfig['time_zone']).strptime("#{params['end_time']}.999999999", '%Y-%m-%d %H:%M:%S.%N')
      rescue ArgumentError => e
        return I18n.t('validation.invalid_time_format')
      end
    end

    if params['start_time'].present? || params['end_time'].present?
      return I18n.t('validation.invalid_time_range') if start_time > end_time
      conditions['created_at'] = start_time.utc..end_time.utc
    end

    if params['statuses'].class == String
      status = ::Shop::STATUSES[params['statuses']]
      conditions['status'] = status if status.present?
    elsif params['statuses'].class == Array
      statuses = params['statuses'].to_a.map { |status| ::Shop::STATUSES[status] }.compact
      conditions['status'] = statuses if statuses.present?
    end

    if params['deleted'].present?
      return 'Invalid param - deleted' unless %w(true false).include? params['deleted'].to_s
      conditions['deleted'] = params['deleted'].to_s == 'true'
    end

    return conditions
  end
end
