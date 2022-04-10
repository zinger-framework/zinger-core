class ItemVariant < ApplicationRecord
  belongs_to :item, optional: true
  validate :validations

  def as_json purpose = nil, options = {}
    resp = { 'id' => ShortUUID.shorten(self.id), 'value' => self.variant_value, 'price' => self.actual_price.to_f, 
      'reference_id' => self.variant_name, 'availability' => self.stock_availability }
    case purpose
    when 'admin_item_variant', 'platform_item_variant'
      return resp
    end

    return resp
  end

  private

  def validations
    %w(item_type variant_name variant_value actual_price).each do |key|
      errors.add(key.to_sym, I18n.t('validation.required', param: key)) if self.send(key).blank?
    end

    validate_item_type
    unless ItemConfig.exists?(item_type: self.item_type, key: 'variant', value: self.variant_name)
      errors.add(:variant_name, I18n.t('validation.invalid', param: 'variant_name'))
    end

    variant_names = ItemVariant.where(item_id: self.item_id, item_type: self.item_type).where.not(variant_name: self.variant_name).distinct.pluck(:variant_name)
    errors.add(:variant_name, I18n.t('item.variant.name_not_allowed', variant_name: variant_names.join(', '))) if variant_names.present?

    if ItemVariant.exists?(item_id: self.item_id, item_type: self.item_type, variant_name: self.variant_name, variant_value: self.variant_value)
      errors.add(:variant_name, I18n.t('item.variant.already_exist', variant_name: self.variant_name, variant_value: self.variant_value))
    end
  end
end
