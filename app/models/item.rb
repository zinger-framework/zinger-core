class Item < ApplicationRecord
  enum status: { inactive: 1, active: 2 }, _prefix: true
  LIMITS = { cover_photos: 10 }
  # TODO: Move limit to shop-level config - Logesh

  default_scope { where(deleted: false) }
  belongs_to :shop, optional: true
  has_many :item_variants
  validate :validations

  def as_json purpose = nil
    resp = { 'id' => ShortUUID.shorten(self.id), 'name' => self.name, 'item_type' => self.item_type, 'status' => self.status,
      'icon' => self.icon.present? ? Core::Storage.fetch_url(self.icon_key_path) : nil, 
      'category' => self.category }
    case purpose
    when 'admin_item_list', 'platform_item_list'
      return resp
    when 'admin_item', 'platform_item'
      resp = resp.merge({ 'description' => self.description, 'variants' => self.item_variants.as_json("#{purpose}_variant"),
        'cover_photos' => self.cover_photos.to_a.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
          'url' => Core::Storage.fetch_url(self.cover_photo_key_path(cover_photo)) } }, 
        'meta_data' => self.meta['meta_data'].to_h, 'filterable_fields' => self.fetch_filterable_fields,
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

  def fetch_filterable_fields
    self.meta['filterable_fields'].to_a.map { |reference_id, value| { 'reference_id' => reference_id, 'value' => value } }
  end

  private

  def validations
    %w(name category item_type).each do |key|
      errors.add(key.to_sym, I18n.t('validation.invalid', param: key)) if self.send(key).blank?
    end
    errors.add(:description, I18n.t('item.description_long')) if self.description.present? && self.description.length > 250
    errors.add(:status, I18n.t('validation.invalid', param: 'status')) if Item.statuses[self.status].nil?
    if self.status_active?
      errors.add(:status, I18n.t('validation.required', param: 'Variant')) if !self.item_variants.exists?
      errors.add(:status, I18n.t('validation.required', param: 'Filters')) if self.meta['filterable_fields'].blank?
    end
    
    validate_item_type

    item_config = ItemConfig.fetch_all_configs('admin_item', { 'item_type' => self.item_type })[self.item_type]
    self.category = self.category.to_s.underscore.parameterize.dasherize
    if (self.new_record? || self.category_changed?) && item_config['category'][self.category].nil?
      errors.add(:category, I18n.t('validation.invalid', param: 'category'))
    end

    if self.meta['filterable_fields'].present?
      missing_filters = item_config['filter'].keys.select { |filter| self.meta['filterable_fields'][filter].blank? }
      errors.add(:filterable_fields, I18n.t('validation.required', param: missing_filters.join(', '))) if missing_filters.present?

      invalid_filters = self.meta['filterable_fields'].keys - item_config['filter'].keys
      errors.add(:filterable_fields, I18n.t('item.filter_not_allowed', fields: item_config['filter'].keys.join(', '))) if invalid_filters.present?
    end
  end
end
