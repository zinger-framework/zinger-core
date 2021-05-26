class CreateAdminUserSessions < ActiveRecord::Migration[6.0]
  def change
    create_table :admin_user_sessions, id: false do |t|
      t.string :token, primary_key: true
      t.jsonb :meta, default: {}
      t.string :login_ip
      t.string :user_agent
      t.column :admin_user_id, 'BIGINT'
      t.timestamps

      t.index :admin_user_id
    end
  end
end
