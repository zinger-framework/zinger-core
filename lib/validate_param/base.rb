class ValidateParam::Base
  def self.load_conditions params, options = {}
    conditions, errors, start_date, end_date = [], {}, nil, nil

    if params['start_date'].present?
      begin
        start_date = Time.find_zone(PlatformConfig['time_zone']).strptime(params['start_date'], '%Y-%m-%d')
        conditions << ActiveRecord::Base.sanitize_sql_array(["created_at >= ?", start_date])
      rescue ArgumentError => e
        (errors['start_date'] = []) << I18n.t('validation.invalid_date_format')
      end
    end

    if params['end_date'].present?
      begin
        end_date = Time.find_zone(PlatformConfig['time_zone']).strptime(params['end_date'], '%Y-%m-%d')
        conditions << ActiveRecord::Base.sanitize_sql_array(["created_at <= ?", end_date.end_of_day])
      rescue ArgumentError => e
        (errors['end_date'] = []) << I18n.t('validation.invalid_date_format')
      end
    end

    (errors['start_date'] = []) << I18n.t('validation.invalid_date_range') if start_date.present? && end_date.present? && start_date > end_date
    (errors['page_size'] = []) << I18n.t('validation.invalid', param: 'page size') if params['page_size'].present? && 
      (params['page_size'].to_i <= 0 || params['page_size'].to_i.to_s != params['page_size'].to_s)

    sort_order = params['sort_order'].to_s.upcase == 'DESC' ? 'DESC' : 'ASC'
    conditions << ActiveRecord::Base.sanitize_sql_array(["id = ?", params['id']]) if params['id'].present?
    conditions << ActiveRecord::Base.sanitize_sql_array(["id #{sort_order == 'DESC' ? '<' : '>'}= ?", params['next_id']]) if params['next_id'].present?

    return conditions, sort_order, errors
  end
end
