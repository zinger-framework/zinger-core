class Order < ApplicationRecord
  enum order_status: { created: 1, placed: 2, delivery_pending: 3, delivered: 4, cancelled: 5 }, _prefix: true
  enum payment_status: { pending: 1, completed: 2, failed: 3, refund_pending: 4, refund_failed: 5, refund_completed: 6 }, _prefix: true
  END_ORDER_STATUSES = %w(delivered cancelled)
  ADDRESS_PARAMS = %w(name street area city state pincode)

  scope :undeleted, -> { where(deleted: false) }

  belongs_to :shop, optional: true
  belongs_to :customer, optional: true
  has_many :order_items
  has_many :order_transactions

  validate :validations

  def as_json
    resp = super
    resp['id'] = ShortUUID.shorten(self.id)
    resp['items'] = self.order_items.as_json
    resp['transactions'] = self.order_transactions.as_json
    %w(billing_addr shipping_addr).each { |key| resp[key] = resp[key].sort.to_h if resp[key].present? }
    %w(price rating tax).each { |key| resp[key] = resp[key].to_f }
    %w(order_placed_time created_at updated_at).each { |key| resp[key] = resp[key].in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') if resp[key].present? }

    return resp.sort.to_h
  end

  def calculate_price
    self.price = self.order_items.sum(:total_price).to_f.round(2)
  end

  def set_shop _shop_id
    self.shop_id = _shop_id
  end

  private

  def validations
    if self.order_status_placed?
      errors.add(:order_items, I18n.t('validation.required', param: 'Item')) if self.order_items.empty?

      self.order_placed_time = Time.now if self.order_status_was != 'placed'
    end

    errors.add(:rating, I18n.t('order.rating_not_allowed')) if self.rating_changed? && !END_ORDER_STATUSES.include?(self.order_status)
  end
end
