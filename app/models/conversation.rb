class Conversation < ApplicationRecord
  PURPOSES = { 'SHOP_REJECT' => 1, 'SHOP_BLOCK' => 2, 'SHOP_DELETE' => 3 }

  default_scope { where(deleted: false).order(id: :desc) }

  belongs_to :sender, polymorphic: true
  belongs_to :receiver, polymorphic: true

  def as_json purpose = nil
    case purpose
    when 'shop_reject', 'shop_block', 'shop_delete'
      return { 'id' => self.id, 'platform_user' => self.sender.as_json('conversation'), 'message' => self.message, 
        'time' => self.created_at.in_time_zone(PlatformConfig['time_zone']).strftime('%Y-%m-%d %H:%M:%S') }
    end
  end
end
