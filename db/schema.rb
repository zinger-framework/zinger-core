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

ActiveRecord::Schema.define(version: 2020_04_13_092244) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "user_sessions", primary_key: "token", id: :string, force: :cascade do |t|
    t.jsonb "meta", default: {}
    t.string "login_ip"
    t.string "user_agent"
    t.bigint "user_id"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["user_id"], name: "index_user_sessions_on_user_id"
  end

  create_table "users", force: :cascade do |t|
    t.string "email"
    t.string "mobile"
    t.string "password_digest"
    t.string "otp_secret_key"
    t.boolean "two_factor_enabled"
    t.boolean "deleted", default: false
    t.integer "status", limit: 2, default: 1
    t.datetime "created_at", precision: 6, null: false
    t.datetime "updated_at", precision: 6, null: false
    t.index ["email"], name: "index_users_on_email"
    t.index ["mobile"], name: "index_users_on_mobile"
  end

end
