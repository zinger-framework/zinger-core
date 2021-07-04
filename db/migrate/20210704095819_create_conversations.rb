class CreateConversations < ActiveRecord::Migration[6.0]
  def change
    create_table :conversations do |t|
      t.column :sender_id, 'BIGINT'
      t.string :sender_type
      t.column :receiver_id, 'BIGINT'
      t.string :receiver_type
      t.string :message
      t.column :purpose, 'SMALLINT'
      t.boolean :deleted, default: false
      t.timestamps

      t.index [:purpose, :receiver_id, :receiver_type], name: 'index_conversations_on_receiver_id_receiver_type'
    end
  end
end
