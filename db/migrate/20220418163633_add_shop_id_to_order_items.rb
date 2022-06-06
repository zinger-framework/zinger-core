class AddShopIdToOrderItems < ActiveRecord::Migration[6.0]
  def change
    add_column :order_items, :shop_id, 'BIGINT'
  end
end
