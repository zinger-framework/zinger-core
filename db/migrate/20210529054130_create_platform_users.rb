class CreatePlatformUsers < ActiveRecord::Migration[6.0]
  DEFAULT_PASSWORD = 'admin123'

  def up
    create_table :platform_users do |t|
      t.string :name
      t.string :email
      t.string :mobile
      t.string :password_digest
      t.boolean :two_fa_enabled, default: false
      t.column :status, 'SMALLINT', default: 1
      t.boolean :deleted, default: false
      t.timestamps

      t.index :email
    end
    
    PlatformUser.create!(name: 'Admin', email: 'admin@zinger.pw', password: DEFAULT_PASSWORD, password_confirmation: DEFAULT_PASSWORD, 
      status: PlatformUser::STATUSES['ACTIVE'])
  end

  def down
    drop_table :platform_users
  end
end
