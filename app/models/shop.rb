class Shop < ApplicationRecord
  STATUSES = { 'DRAFT' => 1, 'PENDING' => 2, 'ACTIVE' => 3, 'BLOCKED' => 4, 'REJECTED' => 5, 'INACTIVE' => 6 }
  CATEGORIES = { 'GROCERY' => 1, 'PHARMACY' => 2, 'RESTAURANT' => 3, 'OTHERS' => 4 }
  PENDING_STATUSES = [STATUSES['DRAFT'], STATUSES['PENDING'], STATUSES['REJECTED']]

  scope :undeleted, -> { where(deleted: false) }
  # searchkick word_start: ['name'], locations: ['location'], default_fields: ['status', 'deleted']
  # TODO: Uncomment when elastic search is integrated - Logesh

  has_one :shop_detail
  has_and_belongs_to_many :admin_users
  has_many :rejected_conversations, -> { preload(:sender).where(purpose: Conversation::PURPOSES['SHOP_REJECT']) }, class_name: 'Conversation', as: :receiver
  has_many :blocked_conversations, -> { preload(:sender).where(purpose: Conversation::PURPOSES['SHOP_BLOCK']) }, class_name: 'Conversation', as: :receiver
  has_many :deleted_conversations, -> { preload(:sender).where(purpose: Conversation::PURPOSES['SHOP_DELETE']) }, class_name: 'Conversation', as: :receiver

  validate :validations
  after_create :add_shop_detail
  after_commit :commit_callbacks

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
    when 'admin_shop', 'platform_shop'
      resp = { 'id' => self.id, 'name' => self.name, 'icon' => self.icon.present? ? Core::Storage.fetch_url(self.icon_key_path) : nil, 
        'tags' => self.tags.to_s.split(' ').map(&:titlecase), 'category' => CATEGORIES.key(self.category), 'email' => self.email, 
        'status' => STATUSES.key(self.status), 'updated_at' => self.updated_at.in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') }
        .merge(self.shop_detail.as_json("#{purpose}_detail", { 'lat' => self.lat.to_f, 'lng' => self.lng.to_f }))
      if purpose == 'platform_shop'
        resp = resp.merge({ 'deleted' => self.deleted })
        resp['deleted_conversations'] = self.deleted_conversations.map { |conv| conv.as_json('shop_delete') } if self.deleted
      end
      case self.status
      when STATUSES['PENDING'], STATUSES['REJECTED']
        resp['rejected_conversations'] = self.rejected_conversations.map { |conv| conv.as_json('shop_reject') }
      when STATUSES['BLOCKED']
        resp['blocked_conversations'] = self.blocked_conversations.map { |conv| conv.as_json('shop_block') }
      end
      return resp
    end
  end

  def self.fetch_by_id id
    Core::Redis.fetch(Core::Redis::SHOP_BY_ID % { id: id }, { type: Shop }) { Shop.undeleted.find_by_id(id) }
  end

  def icon_key_path
    "shop/icon/#{self.id}/#{self.icon}"
  end

  def is_blocked?
    self.status == STATUSES['BLOCKED']
  end

  private

  def validations
    if self.status_changed?
      invalid_status = case self.status
      when STATUSES['ACTIVE']
        true unless [STATUSES['PENDING'], STATUSES['BLOCKED'], STATUSES['INACTIVE'], STATUSES['REJECTED']].include? self.status_was
      when STATUSES['BLOCKED']
        true unless [STATUSES['ACTIVE'], STATUSES['INACTIVE']].include? self.status_was
      when STATUSES['REJECTED']
        true if self.status_was != STATUSES['PENDING']
      when STATUSES['INACTIVE']
        true if self.status_was != STATUSES['ACTIVE']
      else
        true
      end
      errors.add(:status, I18n.t('validation.invalid', param: 'status')) if invalid_status.present?
    end
    errors.add(:category, I18n.t('validation.invalid', param: 'category')) if self.category.nil?
    errors.add(:email, I18n.t('validation.invalid', param: 'email')) if self.email.present? && !self.email.match(EMAIL_REGEX)
  end

  def add_shop_detail
    self.create_shop_detail
  end

  def commit_callbacks
    if self.saved_change_to_status? && self.status == STATUSES['PENDING']
      MailerWorker.perform_async('platform', { 'subject' => 'Shop Approval is PENDING', 'id' => self.id, 'name' => self.name, 
        'link' => Rails.application.routes.url_helpers.pl_shop_detail_url(host: AppConfig['platform_ui_endpoint'], id: self.id) }.to_json)
    end
    self.clear_cache
  end

  def clear_cache
    Core::Redis.delete(Core::Redis::SHOP_BY_ID % { id: self.id })
  end
end
