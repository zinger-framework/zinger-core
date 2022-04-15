class OrderItem < ApplicationRecord
  belongs_to :order, optional: true
  belongs_to :item, optional: true
  belongs_to :item_variant, optional: true

  def as_json
    resp = super
    resp = resp.except('order_id', 'created_at', 'updated_at')
    %w(actual_price total_price).each { |key| resp[key] = resp[key].to_f }

    return resp
  end
end
