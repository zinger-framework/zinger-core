class CreateOrderTransaction < ActiveRecord::Migration[6.0]
  def change
    create_table :order_transactions, id: :uuid, default: nil do |t|
      t.uuid :order_id
      t.decimal :amount, precision: 10, scale: 2
      t.column :txn_status, 'SMALLINT', default: 1
      t.column :txn_type, 'SMALLINT'
      t.column :payment_method, 'SMALLINT'
      t.string :bank_ref_num
      t.datetime :txn_time
      t.jsonb :meta, default: {}

      t.timestamps
    end
  end
end
