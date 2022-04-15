class CreateOrderItems < ActiveRecord::Migration[6.0]
  def change
    create_table :order_items, id: :uuid, default: nil do |t|
      t.uuid :order_id
      t.uuid :item_id
      t.string :item_name
      t.uuid :item_variant_id
      t.string :item_variant_name
      t.string :item_variant_value
      t.integer :quantity
      t.decimal :actual_price, precision: 10, scale: 2
      t.decimal :total_price, precision: 10, scale: 2
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
