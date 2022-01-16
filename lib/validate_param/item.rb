class ValidateParam::Item < ValidateParam::Base
  def self.load_conditions params, options = {}
    conditions, sort_order, errors = super(params, options)

    if params['categories'].present?
      categories = Array.wrap(params['categories']).map { |category| category.underscore.parameterize.dasherize }.compact
      conditions << ActiveRecord::Base.sanitize_sql_array(["category IN ('%s')" % categories.join("', '")]) if categories.present?
    end

    conditions << ActiveRecord::Base.sanitize_sql_array(["item_type = ?", params['item_type']]) if params['item_type'].present?

    query = options['shop'].items.all
    query = query.status_active if params['include_inactive'] != 'true'
    query = query.where(conditions.join(' AND ')) if conditions.present?
    
    return query.order("id #{sort_order}")
  end
end
