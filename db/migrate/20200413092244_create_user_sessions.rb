class CreateUserSessions < ActiveRecord::Migration[5.2]
  def change
    create_table :user_sessions, id: false do |t|
      t.string :token, primary_key: true
      t.jsonb :meta, default: {}
      t.string :login_ip
      t.string :user_agent
      t.column :user_id, 'BIGINT'
      t.timestamps

      t.index :user_id
    end
  end
end
