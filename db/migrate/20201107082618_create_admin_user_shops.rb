class CreateAdminUserShops < ActiveRecord::Migration[6.0]
  def change
    create_table :admin_users_shops, id: false do |t|
      t.belongs_to :admin_user
      t.belongs_to :shop
      t.timestamps
    end
  end
end
