class CreateOrders < ActiveRecord::Migration[6.0]
  def change
    create_table :orders, id: :uuid, default: nil do |t|
      t.column :shop_id, 'BIGINT'
      t.column :customer_id, 'BIGINT'
      t.decimal :price, precision: 10, scale: 2
      t.decimal :tax, precision: 10, scale: 2
      t.column :order_status, 'SMALLINT', default: 1
      t.column :payment_status, 'SMALLINT', default: 1
      t.jsonb :shipping_addr, default: {}
      t.jsonb :billing_addr, default: {}
      t.datetime :order_placed_time
      t.decimal :rating, precision: 2, scale: 1
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
