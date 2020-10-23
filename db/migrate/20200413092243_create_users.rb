class CreateUsers < ActiveRecord::Migration[6.0]
  def change
    create_table :users do |t|
      t.string :email
      t.string :mobile
      t.string :password_digest
      t.string :otp_secret_key
      t.boolean :two_factor_enabled
      t.boolean :deleted, default: false
      t.column :status, 'SMALLINT', default: 1

      t.timestamps

      t.index :email
      t.index :mobile
    end
  end
end
