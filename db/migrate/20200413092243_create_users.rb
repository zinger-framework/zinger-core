class CreateUsers < ActiveRecord::Migration[6.0]
  def change
    create_table :users do |t|
      t.string :user_name
      t.string :password_digest
      t.string :email
      t.string :mobile
      t.string :otp_secret_key
      t.boolean :two_factor_enabled

      t.timestamps

      t.index :email, unique: true
      t.index :user_name, unique: true
      t.index :mobile, unique: true
    end
  end
end
