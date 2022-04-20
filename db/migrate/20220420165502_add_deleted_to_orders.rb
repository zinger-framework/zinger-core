class AddDeletedToOrders < ActiveRecord::Migration[6.0]
  def change
    add_column :orders, :deleted, :boolean, default: false
  end
end
