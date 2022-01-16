class ApplicationRecord < ActiveRecord::Base
  self.abstract_class = true

  before_create :set_id

  private
  
  def set_id
    self.id ||= LSUUID.generate
  end

  def validate_item_type
    errors.add(:item_type, I18n.t('validation.invalid', param: 'item_type')) unless PlatformConfig['item_types'].to_a.include?(self.item_type.to_s)
  end
end
