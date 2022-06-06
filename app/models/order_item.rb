class OrderItem < ApplicationRecord
  belongs_to :order, optional: true
  belongs_to :shop, optional: true
  belongs_to :item, optional: true
  belongs_to :item_variant, optional: true

  validate :validations
  before_save :set_attributes
  after_create_commit :create_callbacks
  after_update_commit :update_callbacks
  after_destroy_commit :destroy_callbacks

  def as_json
    resp = super
    %w(id item_id item_variant_id).each { |key| resp[key] = ShortUUID.shorten(self.send(key)) }
    resp = resp.except('shop_id', 'order_id', 'created_at', 'updated_at')
    %w(actual_price total_price).each { |key| resp[key] = resp[key].to_f }

    return resp.sort.to_h
  end

  private

  def validations
    if self.item.nil? || !self.item.is_active?
      errors.add(:item_id, I18n.t('item.not_found'))
    elsif !self.item.shop.is_active?
      errors.add(:shop_id, I18n.t('shop.not_found'))
    end
    
    errors.add(:item_variant_id, I18n.t('item.variant.not_found')) if self.item_variant.nil?
  end

  def set_attributes
    self.item_name = self.item.name
    self.shop = self.item.shop
    self.item_variant_name = self.item_variant.variant_name
    self.item_variant_value = self.item_variant.variant_value
    self.actual_price = self.item_variant.actual_price.to_f
    self.total_price = self.quantity * self.actual_price
  end

  def create_callbacks
    self.commit_callbacks
  end

  def update_callbacks
    self.commit_callbacks if self.saved_changes_to_total_price?
  end

  def destroy_callbacks
    self.commit_callbacks
  end

  def commit_callbacks
    self.order.calculate_price
    self.order.set_shop(self.shop_id)
    self.order.save!
  end
end
