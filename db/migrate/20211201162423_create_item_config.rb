class CreateItemConfig < ActiveRecord::Migration[6.0]
  def change
    create_table :item_configs, id: :uuid, default: nil do |t|
      t.string :item_type # food
      t.string :key # category
      t.string :value # chinese
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
