CREATE DATABASE ifJlo6XgsN;

USE ifJlo6XgsN;
<<<<<<< HEAD
USE food;
=======
>>>>>>> 9a30e262dfe3bdc5c555cda7012971823b062317

-- DROP TABLE users_college;
-- DROP TABLE users_shop;
-- DROP TABLE orders_item;
-- DROP TABLE rating;
-- DROP TABLE configurations;

-- DROP TABLE orders;
-- DROP TABLE transactions;
-- DROP TABLE item;
-- DROP TABLE users;
-- DROP TABLE shop;
-- DROP TABLE college;

####################################################

CREATE TABLE college (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  icon_url VARCHAR(64) NOT NULL,
  address VARCHAR(256) NOT NULL,
  is_delete INT DEFAULT 0,
  CONSTRAINT college_id_pk PRIMARY KEY (id)
);

CREATE TABLE shop (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) UNIQUE NOT NULL,
  photo_url VARCHAR(64) DEFAULT NULL,
  mobile VARCHAR(10) NOT NULL,
  college_id INT NOT NULL,
  opening_time TIME NOT NULL,
  closing_time TIME NOT NULL,
  is_delete INT DEFAULT 0,
  CONSTRAINT shop_id_pk PRIMARY KEY (id),
  CONSTRAINT shop_college_id_fk FOREIGN KEY (college_id) REFERENCES college(id)
);

CREATE TABLE users(
  mobile VARCHAR(10),
  name VARCHAR(32) DEFAULT NULL,
  email VARCHAR(64) DEFAULT NULL,
  oauth_id VARCHAR(64) UNIQUE DEFAULT NULL,
  role ENUM('CUSTOMER','SELLER','SHOP_OWNER','SUPER_ADMIN'),
  is_delete INT DEFAULT 0,
  CONSTRAINT users_mobile_pk PRIMARY KEY(mobile)
);

CREATE TABLE item (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  price DOUBLE NOT NULL,
  photo_url VARCHAR(64) DEFAULT NULL,
  category VARCHAR(16) NOT NULL,
  shop_id INT NOT NULL,
  is_veg INT DEFAULT 0,
  is_available INT DEFAULT 0,
  is_delete INT DEFAULT 0,
  CONSTRAINT item_id_pk PRIMARY KEY(id),
  CONSTRAINT item_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

CREATE TABLE transactions (
  transaction_id VARCHAR(64) NOT NULL,
  bank_transaction_id VARCHAR(64) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  response_code VARCHAR(10) NOT NULL,
  response_message VARCHAR(500) NOT NULL,
  gateway_name VARCHAR(15) NOT NULL,
  bank_name VARCHAR(500) NOT NULL,
  payment_mode VARCHAR(15) NOT NULL,
  checksum_hash VARCHAR(108) NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT transactions_transaction_id_pk PRIMARY KEY (transaction_id)
);

CREATE TABLE orders (
  id VARCHAR(16) NOT NULL,
  mobile VARCHAR(10) NOT NULL,
  transaction_id VARCHAR(64) UNIQUE NOT NULL,
  shop_id INT NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status ENUM('PENDING', 'TXN_FAILURE', 'PLACED', 'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER', 'READY', 'OUT_FOR_DELIVERY', 'COMPLETED', 'DELIVERED'),
  last_status_updated_time DATETIME DEFAULT NULL,
  price DOUBLE NOT NULL,
  delivery_price DOUBLE DEFAULT NULL,
  delivery_location VARCHAR(128) DEFAULT NULL,
  cooking_info VARCHAR(128) DEFAULT NULL,
  rating DOUBLE DEFAULT NULL,
  secret_key VARCHAR(10) DEFAULT NULL,
  CONSTRAINT orders_id_pk PRIMARY KEY (id),
  CONSTRAINT orders_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile),
  CONSTRAINT orders_transaction_id_fk FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
  CONSTRAINT orders_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop(id)
);

####################################################

CREATE TABLE users_shop (
   mobile VARCHAR(10) NOT NULL,
   shop_id INT NOT NULL,
   CONSTRAINT users_shop_mobile_shop_id_pk PRIMARY KEY(mobile, shop_id),
   CONSTRAINT users_shop_mobile_fk FOREIGN KEY(mobile) REFERENCES users(mobile),
   CONSTRAINT users_shop_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

CREATE TABLE users_college (
   mobile VARCHAR(10) NOT NULL,
   college_id INT NOT NULL,
   CONSTRAINT users_college_mobile_pk PRIMARY KEY(mobile),
   CONSTRAINT users_college_mobile_fk FOREIGN KEY(mobile) REFERENCES users(mobile),
   CONSTRAINT users_college_college_id_fk FOREIGN KEY (college_id) REFERENCES college(id)
);

CREATE TABLE orders_item (
  order_id VARCHAR(16) NOT NULL,
  item_id INT NOT NULL,
  quantity INT NOT NULL,
  price DOUBLE NOT NULL,
  CONSTRAINT orders_item_order_id_item_id_pk PRIMARY KEY(order_id, item_id),
  CONSTRAINT orders_item_order_id_fk FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT orders_item_item_id_fk FOREIGN KEY (item_id) REFERENCES item(id)
);

####################################################

CREATE TABLE rating (
   shop_id INT NOT NULL,
   rating DOUBLE DEFAULT NULL,
   user_count INT DEFAULT NULL,
   CONSTRAINT rating_shop_id_pk PRIMARY KEY (shop_id),
   CONSTRAINT rating_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

CREATE TABLE configurations (
   shop_id INT NOT NULL,
   delivery_price DOUBLE NOT NULL,
   is_delivery_available INT DEFAULT 1,
   is_order_taken INT DEFAULT 1,
   CONSTRAINT configurations_shop_id_pk PRIMARY KEY(shop_id),
   CONSTRAINT configurations_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

####################################################
