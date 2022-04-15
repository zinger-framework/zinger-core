class ValidateParam::Order < ValidateParam::Base
  def self.load_conditions params, options = {}
    conditions, sort_order, errors = super(params, options)
    return errors if errors.present?

    query = ::Order.all
    query = query.where(conditions.join(' AND ')) if conditions.present?
    
    return query.order("id #{sort_order}")
  end
end
