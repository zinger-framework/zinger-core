class CreateEmployeeSessions < ActiveRecord::Migration[6.0]
  def change
    create_table :employee_sessions, id: false do |t|
      t.string :token, primary_key: true
      t.jsonb :meta, default: {}
      t.string :login_ip
      t.string :user_agent
      t.column :employee_id, 'BIGINT'
      t.timestamps

      t.index :employee_id
    end
  end
end
