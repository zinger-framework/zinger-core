class CreateShopDetail < ActiveRecord::Migration[6.0]
  def change
    create_table :shop_details, id: false do |t|
      t.column :shop_id, 'BIGINT', primary_key: true
      t.json :address, default: {}
      t.string :telephone
      t.string :mobile
      t.time :opening_time
      t.time :closing_time
      t.string :cover_photos, array: true
      t.jsonb :payment, default: {}
      t.jsonb :meta, default: {}
      t.timestamps
    end
  end
end
