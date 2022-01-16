class CreateItemVariant < ActiveRecord::Migration[6.0]
  def change
    create_table :item_variants, id: :uuid, default: nil do |t|
      t.uuid :item_id
      t.string :item_type # food
      t.string :variant_name # size
      t.string :variant_value # small
      t.decimal :actual_price, precision: 10, scale: 2
      t.integer :stock_availability
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
