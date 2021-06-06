# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# This file is the source Rails uses to define your schema when running `rails
# db:schema:load`. When creating a new database, `rails db:schema:load` tends to
# be faster and is potentially less error prone than running all of your
# migrations from scratch. Old migrations may fail to apply correctly if those
# migrations use external dependencies or application code.
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 2021_05_29_054505) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "admin_user_sessions", primary_key: "token", id: :string, force: :cascade do |t|
    t.jsonb "meta", default: {}
    t.string "login_ip"
    t.string "user_agent"
    t.bigint "admin_user_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["admin_user_id"], name: "index_admin_user_sessions_on_admin_user_id"
  end

  create_table "admin_users", force: :cascade do |t|
    t.string "name"
    t.string "email"
    t.string "mobile"
    t.string "password_digest"
    t.boolean "two_fa_enabled", default: false
    t.integer "status", limit: 2, default: 1
    t.boolean "deleted", default: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["email"], name: "index_admin_users_on_email"
  end

  create_table "admin_users_shops", id: false, force: :cascade do |t|
    t.bigint "admin_user_id"
    t.bigint "shop_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["admin_user_id"], name: "index_admin_users_shops_on_admin_user_id"
    t.index ["shop_id"], name: "index_admin_users_shops_on_shop_id"
  end

  create_table "customer_sessions", primary_key: "token", id: :string, force: :cascade do |t|
    t.jsonb "meta", default: {}
    t.string "login_ip"
    t.string "user_agent"
    t.bigint "customer_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["customer_id"], name: "index_customer_sessions_on_customer_id"
  end

  create_table "customers", force: :cascade do |t|
    t.string "name"
    t.string "email"
    t.string "mobile"
    t.string "password_digest"
    t.integer "auth_mode", limit: 2
    t.integer "status", limit: 2, default: 1
    t.boolean "deleted", default: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["email"], name: "index_customers_on_email"
    t.index ["mobile"], name: "index_customers_on_mobile"
  end

  create_table "platform_user_sessions", primary_key: "token", id: :string, force: :cascade do |t|
    t.jsonb "meta", default: {}
    t.string "login_ip"
    t.string "user_agent"
    t.bigint "platform_user_id"
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["platform_user_id"], name: "index_platform_user_sessions_on_platform_user_id"
  end

  create_table "platform_users", force: :cascade do |t|
    t.string "name"
    t.string "email"
    t.string "mobile"
    t.string "password_digest"
    t.boolean "two_fa_enabled", default: false
    t.integer "status", limit: 2, default: 1
    t.boolean "deleted", default: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["email"], name: "index_platform_users_on_email"
  end

  create_table "shop_details", primary_key: "shop_id", id: :bigint, default: nil, force: :cascade do |t|
    t.json "address", default: {}
    t.string "telephone"
    t.string "mobile"
    t.time "opening_time"
    t.time "closing_time"
    t.string "description"
    t.string "cover_photos", array: true
    t.jsonb "payment", default: {}
    t.jsonb "meta", default: {}
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
  end

  create_table "shops", force: :cascade do |t|
    t.string "name"
    t.decimal "lat", precision: 10, scale: 8
    t.decimal "lng", precision: 11, scale: 8
    t.string "icon"
    t.string "tags"
    t.integer "category", limit: 2
    t.string "email"
    t.integer "status", limit: 2, default: 1
    t.boolean "deleted", default: false
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
  end

end
