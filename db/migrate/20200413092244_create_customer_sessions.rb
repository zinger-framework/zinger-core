class CreateCustomerSessions < ActiveRecord::Migration[5.2]
  def change
    create_table :customer_sessions, id: false do |t|
      t.string :token, primary_key: true
      t.jsonb :meta, default: {}
      t.string :login_ip
      t.string :user_agent
      t.column :customer_id, 'BIGINT'
      t.timestamps

      t.index :customer_id
    end
  end
end
