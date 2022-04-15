class Order < ApplicationRecord
  enum order_status: { created: 1, placed: 2, delivery_pending: 3, delivered: 4, cancelled: 5 }, _prefix: true
  enum payment_status: { pending: 1, completed: 2, failed: 3, refund_pending: 4, refund_failed: 5, refund_completed: 6 }, _prefix: true

  belongs_to :shop, optional: true
  belongs_to :customer, optional: true
  has_many :order_items
  has_many :order_transactions

  def as_json
    resp = super
    resp['items'] = self.order_items.as_json
    resp['transactions'] = self.order_transactions.as_json
    %w(price rating tax).each { |key| resp[key] = resp[key].to_f }
    %w(order_placed_time created_at updated_at).each { |key| resp[key] = resp[key].in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') if resp[key].present? }

    return resp
  end
end
