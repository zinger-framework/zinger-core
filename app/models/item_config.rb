class ItemConfig < ApplicationRecord
  KEYS = %w(category filter variant)
  validate :validations

  def as_json purpose = nil, options = {}
    resp = { 'reference_id' => self.value, 'title' => self.meta['title'] }
    case purpose
    when 'admin_item', 'platform_item'
      return resp
    when 'platform_list'
      return resp.merge({ 'id' => ShortUUID.shorten(self.id) })
    when 'platform_detail'
      return resp.merge({ 'item_type' => self.item_type, 'key' => self.key })
    end
  end

  def self.fetch_all_configs purpose, options = {}
    data = {}
    query = ItemConfig.all
    query = query.where(item_type: options['item_type']) if options['item_type'].present?
    
    query.group(:item_type, :key, :value, :id).each do |item_config|
      data[item_config.item_type] ||= {}

      if options['export_to'] == 'array'
        data[item_config.item_type][item_config.key] ||= []
        data[item_config.item_type][item_config.key] << item_config.as_json(purpose)
      else
        data[item_config.item_type][item_config.key] ||= {}
        data[item_config.item_type][item_config.key][item_config.value] = item_config.as_json(purpose)
      end
    end

    return data
  end

  private

  def validations
    %w(item_type key value).each do |key|
      errors.add(key.to_sym, I18n.t('validation.required', param: key)) if self.send(key).blank?
    end

    validate_item_type
    errors.add(:key, I18n.t('validation.invalid', param: self.key)) unless KEYS.include?(self.key)
    
    if ItemConfig.exists?(item_type: self.item_type, key: self.key, value: self.value)
      errors.add(:value, I18n.t('item.config.already_exist', key: self.key, value: self.value))
    end
  end
end
