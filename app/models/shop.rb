class Shop < ApplicationRecord
  STATUSES = { 'ACTIVE' => 1, 'BLOCKED' => 2 }

  default_scope { where(deleted: false) }
  searchkick word_start: ['name'], locations: ['location'], default_fields: ['status', 'deleted']

  has_one :shop_detail
  validate :validations
  after_commit :clear_cache

  def search_data
    { name: self.name, location: { lat: self.lat, lon: self.lng }, tags: self.tags, status: self.status, deleted: self.deleted }
  end

  def as_json purpose = nil
    case purpose
    when 'ui_shop'
      return { id: self.id, name: self.name, icon: Core::Storage.fetch_url(aws_key_path), tags: self.tags.split(' '), area: self.shop_detail.address['area'] }
    when 'ui_shop_detail'
      return { id: self.id, name: self.name, icon: Core::Storage.fetch_url(aws_key_path), tags: self.tags.split(' ') }.merge(self.shop_detail.as_json('ui_shop_detail'))
    when 'admin_shop'
      return { id: self.id, name: self.name, tags: self.tags, status: self.status }.merge(self.shop_detail.as_json('admin_shop_detail'))
    end
  end

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::SHOP_BY_ID % { id: id }, { type: Shop }) { Shop.find_by_id(id) }
  end

  def aws_key_path
    "shop/#{self.id}/#{self.icon}"
  end

  private

  def validations
    self.name = self.name.to_s.strip
    return errors.add(:name, I18n.t('validation.required', param: 'Name')) if self.name.blank?

    self.lat = self.lat.to_f
    return errors.add(:lat, I18n.t('validation.required', param: 'Latitude')) if self.lat.to_i == 0
    self.lng = self.lng.to_f
    return errors.add(:lng, I18n.t('validation.required', param: 'Longitude')) if self.lng.to_i == 0

    self.tags = self.tags.strip.upcase
    return errors.add(:tags, I18n.t('validation.required', param: 'Tags')) if self.tags.blank?
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::SHOP_BY_ID % { id: self.id })
  end
end
