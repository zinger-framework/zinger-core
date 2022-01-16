class CreateItem < ActiveRecord::Migration[6.0]
  def change
    create_table :items, id: :uuid, default: nil do |t|
      t.column :shop_id, 'BIGINT'
      t.string :name
      t.string :description
      t.string :icon
      t.string :cover_photos, array: true
      t.string :item_type # food
      t.string :category # north_indian
      t.column :status, 'SMALLINT', default: 1
      t.boolean :deleted, default: false
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
