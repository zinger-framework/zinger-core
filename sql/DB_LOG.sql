-- DROP TABLE orders_log;
-- DROP TABLE transactions_log;
-- DROP TABLE item_log;
-- DROP TABLE users_log;
-- DROP TABLE shop_log;
-- DROP TABLE place_log;

create table place_log (
	id INT DEFAULT NULL,
    error_code INT NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table shop_log (
	id INT DEFAULT NULL,
    error_code INT NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table users_log (
	id INT DEFAULT NULL,
    error_code INT NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table item_log (
	id INT DEFAULT NULL,
	error_code INT NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);

create table orders_log (
	id INTEGER DEFAULT NULL,
    error_code INT NOT NULL,
	message VARCHAR(128) NOT NULL,
	updated_value VARCHAR(4096) DEFAULT NULL,
	date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	priority ENUM('LOW','MEDIUM','HIGH')
);
