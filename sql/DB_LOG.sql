-- DROP TABLE users_place_log;
-- DROP TABLE users_shop_log;
-- DROP TABLE configurations_log;
--
-- DROP TABLE orders_log;
-- DROP TABLE transactions_log;
-- DROP TABLE item_log;
-- DROP TABLE users_log;
-- DROP TABLE shop_log;
-- DROP TABLE place_log;

create table place_log (
	id INT DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table shop_log (
	id INT DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table users_log (
	users_mobile VARCHAR(10) DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table item_log (
	id INT DEFAULT NULL,
	error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table transactions_log (
	transaction_id VARCHAR(64) DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table orders_log (
	id VARCHAR(16) DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table users_shop_log (
	users_mobile VARCHAR(10) DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table users_place_log (
	users_mobile VARCHAR(10) DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table configurations_log (
	shop_id INT DEFAULT NULL,
    error_code INT NOT NULL,
	mobile VARCHAR(10) NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);
