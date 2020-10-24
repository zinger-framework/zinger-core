class CreateCustomers < ActiveRecord::Migration[6.0]
  def change
    create_table :customers do |t|
      t.string :name
      t.string :email
      t.string :mobile
      t.string :password_digest
      t.column :status, 'SMALLINT', default: 1
      t.boolean :deleted, default: false
      t.timestamps

      t.index :email
      t.index :mobile
    end
  end
end
