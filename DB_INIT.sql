CREATE DATABASE food;

USE food;

DROP TABLE users_college;
DROP TABLE users_shop;
DROP TABLE orders_item;
DROP TABLE rating;
DROP TABLE configurations;

DROP TABLE transactions;
DROP TABLE orders;
DROP TABLE item;
DROP TABLE users;
DROP TABLE shop;
DROP TABLE college;

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
  mobile VARCHAR(10) UNIQUE NOT NULL,
  college_id INT NOT NULL,
  opening_time DATE NOT NULL,
  closing_time DATE NOT NULL,
  is_delete INT DEFAULT 0,
  CONSTRAINT shop_id_pk PRIMARY KEY (id),
  CONSTRAINT shop_college_id_fk FOREIGN KEY (college_id) REFERENCES college(id)
);

CREATE TABLE users(
  id INT AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  email VARCHAR(64) UNIQUE NOT NULL,
  mobile VARCHAR(10) UNIQUE NOT NULL,
  oauth_id VARCHAR(64) UNIQUE NOT NULL,
  access_token VARCHAR(64) NOT NULL,
  role ENUM('CUSTOMER','SELLER'),
  is_delete INT DEFAULT 0,
  CONSTRAINT users_id_pk PRIMARY KEY(id)
);

CREATE TABLE item (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  price DOUBLE NOT NULL,
  photo_url VARCHAR(64) DEFAULT NULL,
  category VARCHAR(32) NOT NULL,
  shop_id INT NOT NULL,
  is_veg INT DEFAULT 0,
  is_available INT DEFAULT 0,
  is_delete INT DEFAULT 0,
  CONSTRAINT item_id_pk PRIMARY KEY(id),
  CONSTRAINT item_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);
    
CREATE TABLE orders (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  shop_id INT NOT NULL,
  date DATE NOT NULL,
  price DOUBLE NOT NULL,
  delivery_price DOUBLE DEFAULT NULL,
  status ENUM('PENDING', 'PLACED','ACCEPTED', 'COMPLETED','DELIVERED', 'CANCELLED BY SELLER', 'CANCELLED BY CUSTOMER', 'TRANSACTION FAILED'),
  rating DOUBLE DEFAULT NULL,
  secret_key VARCHAR(10) DEFAULT NULL,
  CONSTRAINT orders_id_pk PRIMARY KEY (id),
  CONSTRAINT orders_user_id_fk FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT orders_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop(id)
);

CREATE TABLE transactions (
  transaction_id VARCHAR(64) NOT NULL,
  order_id INT NOT NULL,
  merchant_id VARCHAR(20) NOT NULL,
  bank_transaction_id VARCHAR(64) NOT NULL,
  price DOUBLE NOT NULL,
  currency VARCHAR(3) NOT NULL,
  status ENUM('TXN_SUCCESS', 'PENDING', 'TXN_FAILURE'),
  response_code VARCHAR(10) NOT NULL,
  response_message VARCHAR(500) NOT NULL,
  date DATE NOT NULL,
  gateway_name VARCHAR(15) NOT NULL,
  bank_name VARCHAR(500) NOT NULL,
  payment_mode VARCHAR(15) NOT NULL,
  checksum_hash VARCHAR(108) NOT NULL,
  CONSTRAINT transactions_transaction_id_pk PRIMARY KEY (transaction_id),
  CONSTRAINT transactions_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id)
);

####################################################

CREATE TABLE users_shop (
   user_id INT NOT NULL,
   shop_id INT NOT NULL,
   is_delete INT DEFAULT 0,
   CONSTRAINT users_shop_user_id_shop_id_pk PRIMARY KEY(user_id, shop_id),
   CONSTRAINT users_shop_user_id_fk FOREIGN KEY(user_id) REFERENCES users(id),
   CONSTRAINT users_shop_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

 CREATE TABLE users_college (
   user_id INT NOT NULL,
   college_id INT NOT NULL,
   CONSTRAINT users_college_user_id_college_id_pk PRIMARY KEY(user_id, college_id),
   CONSTRAINT users_college_user_id_fk FOREIGN KEY(user_id) REFERENCES users(id),
   CONSTRAINT users_college_college_id_fk FOREIGN KEY (college_id) REFERENCES college(id)
);

CREATE TABLE orders_item (
  order_id INT NOT NULL,
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
   delivery_price DOUBLE DEFAULT NULL,
   is_delivery_available INT DEFAULT 0,
   is_order_taken INT DEFAULT 0,
   CONSTRAINT config_shop_id_pk PRIMARY KEY(shop_id),
   CONSTRAINT config_shop_id_fk FOREIGN KEY(shop_id) REFERENCES shop(id)
);

####################################################
