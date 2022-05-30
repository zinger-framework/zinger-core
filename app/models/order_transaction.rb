class OrderTransaction < ApplicationRecord
  enum txn_status: { not_initiated: 1, pending: 2, completed: 3, failed: 4 }, _prefix: true
  enum txn_type: { payment: 1, refund: 2 }, _prefix: true
  enum payment_method: { credit_card: 1, debit_card: 2, netbanking: 3, wallet: 4, cash: 5, upi: 6 }, _prefix: true

  belongs_to :order, optional: true

  def as_json
    resp = super
    resp = resp.except('order_id')
    resp['id'] = ShortUUID.shorten(self.id)
    %w(amount).each { |key| resp[key] = resp[key].to_f }
    %w(txn_time created_at updated_at).each { |key| resp[key] = resp[key].in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') if resp[key].present? }

    return resp.sort.to_h
  end
end
