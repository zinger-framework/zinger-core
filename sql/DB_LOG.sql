-- DROP TABLE users_college_log;
-- DROP TABLE users_shop_log;
-- DROP TABLE configurations_log;

-- DROP TABLE orders_log;
-- DROP TABLE transactions_log;
-- DROP TABLE item_log;
-- DROP TABLE users_log;
-- DROP TABLE shop_log;
-- DROP TABLE college_log;

create table college_log (
	id INT NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_NAME','UPDATED_ICON_URL','UPDATED_ADDRESS','DELETED'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT college_log_id_fk FOREIGN KEY (id) REFERENCES college(id),
    CONSTRAINT college_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table shop_log (
	id INT NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_NAME','UPDATED_PHOTO_URL','UPDATED_MOBILE','UPDATED_OPENING_TIME','UPDATED_CLOSING_TIME','DELETED'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT shop_log_id_fk FOREIGN KEY (id) REFERENCES shop(id),
    CONSTRAINT shop_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table users_log (
	users_mobile VARCHAR(10) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_NAME','UPDATED_EMAIL','UPDATED_MOBILE','UPDATED_ROLE','DELETED'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_log_users_mobile_fk FOREIGN KEY (users_mobile) REFERENCES users(mobile),
    CONSTRAINT users_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table item_log (
	id INT NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_NAME','UPDATED_PRICE','UPDATED_PHOTO_URL','UPDATED_CATEGORY','UPDATED_IS_VEG','UPDATED_IS_AVAILABLE','DELETED'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT item_log_id_fk FOREIGN KEY (id) REFERENCES item(id),
    CONSTRAINT item_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table transactions_log (
	transaction_id VARCHAR(64) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_RESPONSE_CODE'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transactions_log_id_fk FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    CONSTRAINT transactions_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table orders_log (
	id VARCHAR(16) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED', 'UPDATED_COOKING_INFO', 'UPDATED_RATING', 'UPDATED_SECRET_KEY', 'UPDATED_ACCEPTED', 'UPDATED_COMPLETED', 'UPDATED_DELIVERED', 'UPDATED_CANCELLED_BY_SELLER', 'UPDATED_CANCELLED_BY_CUSTOMER', 'UPDATED_TXN_FAILURE'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT orders_log_id_fk FOREIGN KEY (id) REFERENCES orders(id),
    CONSTRAINT orders_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table users_shop_log (
	users_mobile VARCHAR(10) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_SHOP_ID','DELETED'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_shop_log_users_mobile_fk FOREIGN KEY (users_mobile) REFERENCES users(mobile),
    CONSTRAINT users_shop_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table users_college_log (
	users_mobile VARCHAR(10) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_COLLEGE_ID'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_college_log_users_mobile_fk FOREIGN KEY (users_mobile) REFERENCES users(mobile),
    CONSTRAINT users_college_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);

create table configurations_log (
	shop_id INT NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    message ENUM('CREATED','UPDATED_DELIVERY_PRICE','UPDATED_IS_DELIVERY_AVAILABILE','UPDATED_IS_ORDER_TAKEN'),
    updated_value VARCHAR(128) DEFAULT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT configurations_log_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop(id),
    CONSTRAINT configurations_log_mobile_fk FOREIGN KEY (mobile) REFERENCES users(mobile)
);