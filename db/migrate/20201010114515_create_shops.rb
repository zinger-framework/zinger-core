class CreateShops < ActiveRecord::Migration[6.0]
  def change
    create_table :shops do |t|
      t.string :name
      t.decimal :lat, :precision => 10, :scale => 8
      t.decimal :lng, :precision => 11, :scale => 8
      t.string :icon
      t.string :tags
      t.column :category, 'SMALLINT'
      t.string :email
      t.column :status, 'SMALLINT', default: 1
      t.boolean :deleted, default: false
      t.timestamps
    end
  end
end
