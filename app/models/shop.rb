class Shop < ApplicationRecord
  STATUSES = { 'DRAFT' => 1, 'PENDING' => 2, 'ACTIVE' => 3, 'BLOCKED' => 4, 'REJECTED' => 5, 'INACTIVE' => 6 }
  CATEGORIES = { 'GROCERY' => 1, 'PHARMACY' => 2, 'RESTAURANT' => 3, 'OTHERS' => 4 }
  PENDING_STATUSES = [STATUSES['DRAFT'], STATUSES['PENDING'], STATUSES['REJECTED']]

  default_scope { where(deleted: false) }
  # searchkick word_start: ['name'], locations: ['location'], default_fields: ['status', 'deleted']
  # TODO: Uncomment when elastic search is integrated - Logesh

  has_one :shop_detail
  has_and_belongs_to_many :employees

  validate :validations
  after_create :add_shop_detail
  after_commit :clear_cache

  def search_data
    { name: self.name, location: { lat: self.lat, lon: self.lng }, tags: self.tags, status: self.status, deleted: self.deleted }
  end

  def as_json purpose = nil
    case purpose
    when 'ui_shop'
      return { 'id' => self.id, 'name' => self.name, 'icon' => Core::Storage.fetch_url(self.icon_key_path), 'tags' => self.tags.to_s.split(' ').map(&:titlecase), 
        'area' => self.shop_detail.address['area'] }
    when 'ui_shop_detail'
      return { 'id' => self.id, 'name' => self.name, 'icon' => Core::Storage.fetch_url(self.icon_key_path), 'tags' => self.tags.to_s.split(' ').map(&:titlecase) }
        .merge(self.shop_detail.as_json('ui_shop_detail'))
    when 'admin_shop'
      return { 'id' => self.id, 'name' => self.name, 'icon' => self.icon.present? ? Core::Storage.fetch_url(self.icon_key_path) : nil, 
        'tags' => self.tags.to_s.split(' ').map(&:titlecase), 'category' => CATEGORIES.key(self.category), 'email' => self.email, 'status' => STATUSES.key(self.status) }
        .merge(self.shop_detail.as_json('admin_shop_detail', { 'lat' => self.lat.to_f, 'lng' => self.lng.to_f }))
    end
  end

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::SHOP_BY_ID % { id: id }, { type: Shop }) { Shop.find_by_id(id) }
  end

  def icon_key_path
    "shop/icon/#{self.id}/#{self.icon}"
  end

  private

  def validations
    errors.add(:category, I18n.t('validation.invalid', param: 'category')) if self.category.nil?
    errors.add(:email, I18n.t('validation.invalid', param: 'email')) if self.email.present? && !self.email.match(EMAIL_REGEX)
  end

  def add_shop_detail
    self.create_shop_detail
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::SHOP_BY_ID % { id: self.id })
  end
end
