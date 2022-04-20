class ValidateParam::Order < ValidateParam::Base
  def self.load_conditions params, options = {}
    conditions, sort_order, errors = super(params, options)
    return errors if errors.present?

    if params['order_status'].present?
      order_statuses = Array.wrap(params['order_status']).map { |order_status| ::Order.order_statuses[order_status] }.compact
      conditions << ActiveRecord::Base.sanitize_sql_array(["order_status IN (%s)", order_statuses.join(', ')]) if order_statuses.present?
    end

    if params['payment_status'].present?
      payment_statuses = Array.wrap(params['payment_status']).map { |payment_status| ::Order.payment_statuses[payment_status] }.compact
      conditions << ActiveRecord::Base.sanitize_sql_array(["payment_status IN (%s)", payment_statuses.join(', ')]) if payment_statuses.present?
    end

    query = options['parent'].orders.all
    query = query.undeleted if params['include_deleted'] != 'true'
    query = query.where(conditions.join(' AND ')) if conditions.present?
    
    return query.order("id #{sort_order}")
  end
end
