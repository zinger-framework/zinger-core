class CreateUserSessions < ActiveRecord::Migration[5.2]
  def change
    create_table :user_sessions, id: false do |t|
      t.string :token, primary_key: true
      t.string :login_ip
      t.string :device_os
      t.string :device_app
      t.column :user_id, 'BIGINT'
      t.timestamps

      t.index [:user_id]
    end
  end
end
