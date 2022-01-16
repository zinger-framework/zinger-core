class Item < ApplicationRecord
  enum status: { inactive: 1, active: 2 }, _prefix: true
  LIMITS = { cover_photos: 10 }
  # TODO: Move limit to shop-level config - Logesh

  default_scope { where(deleted: false) }
  belongs_to :shop, optional: true
  has_many :item_variants
  validate :validations

  def as_json purpose = nil, options = {}
    resp = { 'id' => ShortUUID.shorten(self.id), 'name' => self.name, 'item_type' => self.item_type, 'status' => self.status,
      'icon' => self.icon.present? ? Core::Storage.fetch_url(self.icon_key_path) : nil, 
      'category' => options['configs'].to_h[self.item_type].to_h['category'].to_h[self.category].to_h.fetch('title', self.category) }
    case purpose
    when 'admin_item_list', 'platform_item_list'
      return resp
    when 'admin_item', 'platform_item'
      resp = resp.merge({ 'description' => self.description, 'variants' => self.fetch_item_variants('admin_item_variant', options),
        'cover_photos' => self.cover_photos.to_a.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
          'url' => Core::Storage.fetch_url(self.cover_photo_key_path(cover_photo)) } }, 
        'meta_data' => self.meta['meta_data'].to_h, 'filterable_fields' => self.fetch_filterable_fields(item_type, options),
        'created_at' => self.created_at.in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S'),
        'updated_at' => self.updated_at.in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') })
      return resp
    end

    return resp
  end

  def icon_key_path
    "item/icon/#{self.id}/#{self.icon}"
  end

  def cover_photo_key_path cover_photo
    "item/cover_photos/#{self.id}/#{cover_photo}"
  end

  def fetch_item_variants purpose, options = {}
    variants = {}
    self.item_variants.each do |variant|
      variants[variant.variant_name] ||= { 'reference_id' => variant.variant_name, 'values' => [],
        'title' => options['configs'].to_h[variant.item_type].to_h['variant'].to_h[variant.variant_name].to_h.fetch('title', variant.variant_name) }
      variants[variant.variant_name]['values'] << variant.as_json(purpose)
    end

    return variants.values
  end

  def fetch_filterable_fields item_type, options = {}
    return self.meta['filterable_fields'].to_a.map do |reference_id, value|
      { 'reference_id' => reference_id, 'value' => value,
        'title' => options['configs'].to_h[item_type].to_h['filter'].to_h[reference_id].to_h.fetch('title', reference_id) }
    end
  end

  private

  def validations
    %w(name category item_type).each do |key|
      errors.add(key.to_sym, I18n.t('validation.invalid', param: key)) if self.send(key).blank?
    end
    errors.add(:description, I18n.t('item.description_long')) if self.description.present? && self.description.length > 250
    errors.add(:status, I18n.t('validation.invalid', param: 'status')) if self.status.present? && Item.statuses[self.status].nil?
    validate_item_type

    item_config = ItemConfig.fetch_all_configs('admin_item', { 'item_type' => self.item_type })[self.item_type]
    self.category = self.category.to_s.underscore.parameterize.dasherize
    if (self.new_record? || self.category_changed?) && item_config['category'][self.category].nil?
      errors.add(:category, I18n.t('validation.invalid', param: 'category'))
    end

    if self.meta['filterable_fields'].present?
      missing_filters = item_config['filter'].keys - self.meta['filterable_fields'].keys
      errors.add(:filterable_fields, I18n.t('validation.required', param: missing_filters.join(', '))) if missing_filters.present?

      invalid_filters = self.meta['filterable_fields'].keys - item_config['filter'].keys
      errors.add(:filterable_fields, I18n.t('item.filter_not_allowed', fields: item_config['filter'].keys.join(', '))) if invalid_filters.present?
    end
  end
end
