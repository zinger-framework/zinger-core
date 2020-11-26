class CreateEmployeeDetails < ActiveRecord::Migration[6.0]
  def change
    create_table :employee_details, id: false do |t|
      t.column :employee_id, 'BIGINT'
      t.column :shop_id, 'BIGINT'
      t.timestamps

      t.index [:employee_id, :shop_id]
    end
  end
end
