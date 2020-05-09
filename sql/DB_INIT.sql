CREATE DATABASE zinger;
USE zinger;

# DROP TABLE seller_archive;
# DROP TABLE users_invite;
# DROP TABLE users_place;
# DROP TABLE users_shop;
# DROP TABLE orders_item;
# DROP TABLE rating;
# DROP TABLE configurations;
#
# DROP TABLE transactions;
# DROP TABLE orders;
# DROP TABLE item;
# DROP TABLE users;
# DROP TABLE shop;
# DROP TABLE place;

####################################################

CREATE TABLE place
(
    id        INT AUTO_INCREMENT,
    name      VARCHAR(64) UNIQUE NOT NULL,
    icon_url  VARCHAR(512)       NOT NULL,
    address   VARCHAR(256)       NOT NULL,
    is_delete INT DEFAULT 0,
    CONSTRAINT place_id_pk PRIMARY KEY (id)
);

CREATE TABLE shop
(
    id           INT AUTO_INCREMENT,
    name         VARCHAR(32) UNIQUE NOT NULL,
    photo_url    VARCHAR(512) DEFAULT NULL,
    cover_urls   JSON         DEFAULT NULL,
    mobile       VARCHAR(10)        NOT NULL,
    place_id     INT                NOT NULL,
    opening_time TIME               NOT NULL,
    closing_time TIME               NOT NULL,
    is_delete    INT          DEFAULT 0,
    CONSTRAINT shop_id_pk PRIMARY KEY (id),
    CONSTRAINT shop_place_id_fk FOREIGN KEY (place_id) REFERENCES place (id)
);

CREATE TABLE users
(
    id          INT AUTO_INCREMENT,
    mobile      VARCHAR(10) UNIQUE                                               NOT NULL,
    name        VARCHAR(32) DEFAULT NULL,
    email       VARCHAR(64) DEFAULT NULL,
    oauth_id    VARCHAR(64) UNIQUE                                               NOT NULL,
    notif_token JSON        DEFAULT NULL,
    role        ENUM ('CUSTOMER','SELLER','SHOP_OWNER','DELIVERY','SUPER_ADMIN') NOT NULL,
    is_delete   INT         DEFAULT 0,
    CONSTRAINT users_id_pk PRIMARY KEY (id)
);

CREATE TABLE item
(
    id           INT AUTO_INCREMENT,
    name         VARCHAR(32) NOT NULL,
    price        DOUBLE      NOT NULL,
    photo_url    VARCHAR(512) DEFAULT NULL,
    category     VARCHAR(16) NOT NULL,
    shop_id      INT         NOT NULL,
    is_veg       INT          DEFAULT 1,
    is_available INT          DEFAULT 1,
    is_delete    INT          DEFAULT 0,
    CONSTRAINT item_name_shop_id_pk PRIMARY KEY (name, shop_id),
    CONSTRAINT item_id_uq UNIQUE (id),
    CONSTRAINT item_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

CREATE TABLE orders
(
    id                INT AUTO_INCREMENT,
    user_id           INT    NOT NULL,
    shop_id           INT    NOT NULL,
    date              TIMESTAMP                              DEFAULT CURRENT_TIMESTAMP,
    status            ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') DEFAULT NULL,
    price             DOUBLE NOT NULL,
    delivery_price    DOUBLE                                 DEFAULT NULL,
    delivery_location VARCHAR(128)                           DEFAULT NULL,
    cooking_info      VARCHAR(128)                           DEFAULT NULL,
    rating            DOUBLE(2, 1)                           DEFAULT NULL,
    feedback          VARCHAR(1024)                          DEFAULT NULL,
    secret_key        VARCHAR(10)                            DEFAULT NULL,
    CONSTRAINT orders_id_pk PRIMARY KEY (id),
    CONSTRAINT orders_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT orders_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

CREATE TABLE transactions
(
    transaction_id      VARCHAR(64)  NOT NULL,
    order_id            INT          NOT NULL,
    bank_transaction_id VARCHAR(64)  NOT NULL,
    currency            VARCHAR(3)   DEFAULT NULL,
    response_code       VARCHAR(10)  NOT NULL,
    response_message    VARCHAR(500) NOT NULL,
    gateway_name        VARCHAR(15)  DEFAULT NULL,
    bank_name           VARCHAR(500) DEFAULT NULL,
    payment_mode        VARCHAR(15)  DEFAULT NULL,
    checksum_hash       VARCHAR(108) DEFAULT NULL,
    date                TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transactions_transaction_id_pk PRIMARY KEY (transaction_id),
    CONSTRAINT transactions_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id)
);

####################################################

CREATE TABLE users_shop
(
    user_id INT NOT NULL,
    shop_id INT NOT NULL,
    CONSTRAINT users_shop_user_id_shop_id_pk PRIMARY KEY (user_id, shop_id),
    CONSTRAINT users_shop_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT users_shop_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

CREATE TABLE users_place
(
    user_id  INT NOT NULL,
    place_id INT NOT NULL,
    CONSTRAINT users_place_user_id_pk PRIMARY KEY (user_id),
    CONSTRAINT users_place_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT users_place_place_id_fk FOREIGN KEY (place_id) REFERENCES place (id)
);

CREATE TABLE orders_item
(
    order_id INT    NOT NULL,
    item_id  INT    NOT NULL,
    quantity INT    NOT NULL,
    price    DOUBLE NOT NULL,
    CONSTRAINT orders_item_order_id_item_id_pk PRIMARY KEY (order_id, item_id),
    CONSTRAINT orders_item_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT orders_item_item_id_fk FOREIGN KEY (item_id) REFERENCES item (id)
);

create table orders_status
(
    order_id     INT                                         NOT NULL,
    status       ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') NOT NULL,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT orders_status_order_id_status_pk PRIMARY KEY (order_id, status),
    CONSTRAINT orders_status_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id)
);

####################################################

CREATE TABLE rating
(
    shop_id    INT NOT NULL,
    rating     DOUBLE(2, 1) DEFAULT 0,
    user_count INT          DEFAULT 0,
    CONSTRAINT rating_shop_id_pk PRIMARY KEY (shop_id),
    CONSTRAINT rating_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

CREATE TABLE configurations
(
    shop_id               INT         NOT NULL,
    merchant_id           VARCHAR(32) NOT NULL,
    delivery_price        DOUBLE DEFAULT 0.0,
    is_delivery_available INT    DEFAULT 1,
    is_order_taken        INT    DEFAULT 1,
    CONSTRAINT configurations_shop_id_pk PRIMARY KEY (shop_id),
    CONSTRAINT configurations_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);


CREATE TABLE seller_archive
(
    user_id    INT NOT NULL,
    shop_id    INT NOT NULL,
    deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT seller_archive_user_id_shop_id_pk PRIMARY KEY (user_id, shop_id),
    CONSTRAINT seller_archive_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT seller_archive_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

create table application_log
(
    request_type    ENUM ('GET', 'POST', 'PUT',
        'PATCH', 'DELETE')   NOT NULL,
    endpoint_url    VARCHAR(1024) DEFAULT NULL,
    request_header  LONGTEXT NOT NULL,
    request_object  LONGTEXT NOT NULL,
    response_object LONGTEXT NOT NULL,
    date            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

####################################################

CREATE TRIGGER seller_archive
    AFTER DELETE
    ON users_shop
    FOR EACH ROW
    INSERT INTO seller_archive(user_id, shop_id)
    VALUES (OLD.user_id, OLD.shop_id);

CREATE TRIGGER new_rating
    AFTER INSERT
    ON shop
    FOR EACH ROW
    INSERT INTO rating(shop_id)
    VALUES (NEW.id);

DELIMITER $$
CREATE TRIGGER notif_update
    BEFORE UPDATE
    ON users
    FOR EACH ROW
BEGIN
    DECLARE actual_notif_token JSON DEFAULT NULL;
    DECLARE actual_notif_token_length BIGINT UNSIGNED DEFAULT NULL;

    SELECT notif_token
    INTO actual_notif_token
    FROM users
    where id = NEW.id;

    IF actual_notif_token IS NULL AND NEW.notif_token IS NOT NULL THEN
        SELECT JSON_ARRAY(NEW.notif_token) INTO actual_notif_token;
    ELSEIF JSON_CONTAINS(actual_notif_token, NEW.notif_token) = 0 THEN
        SET actual_notif_token_length = JSON_LENGTH(actual_notif_token);

        IF actual_notif_token_length >= 5 THEN
            SELECT JSON_REMOVE(actual_notif_token, '$[0]') INTO actual_notif_token;
        END IF;
        SELECT JSON_ARRAY_APPEND(actual_notif_token, '$', NEW.notif_token) INTO actual_notif_token;
    END IF;
    SET NEW.notif_token = actual_notif_token;
END;
$$

DELIMITER $$
CREATE TRIGGER order_time_rating_update
    BEFORE UPDATE
    ON orders
    FOR EACH ROW
BEGIN
    IF (NEW.status = 'PLACED') OR (NEW.status = 'PENDING') OR (NEW.status = 'TXN_FAILURE') THEN
        SET NEW.date = CURRENT_TIMESTAMP;
    END IF;
    IF NEW.rating IS NOT NULL THEN
        BEGIN
            DECLARE actual_status ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
                'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
                'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
                'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') DEFAULT NULL;

            SELECT status
            INTO actual_status
            FROM orders
            where id = NEW.id;

            IF (OLD.rating IS NOT NULL) THEN
                SIGNAL SQLSTATE '02000' SET MESSAGE_TEXT = 'Error: Rating cannot be updated if already done!';
            ELSEIF ((actual_status IS NULL) OR ((actual_status != 'COMPLETED') AND
                                                (actual_status != 'DELIVERED') AND
                                                (actual_status != 'CANCELLED_BY_USER') AND
                                                (actual_status != 'CANCELLED_BY_SELLER') AND
                                                (actual_status != 'REFUND_COMPLETED'))) THEN
                SIGNAL SQLSTATE '02000' SET MESSAGE_TEXT =
                        'Error: Rating cannot be updated before the order completes!';
            END IF;
        END;
    END IF;
END;
$$

DELIMITER $$
CREATE TRIGGER order_status_rating_update
    AFTER UPDATE
    ON orders
    FOR EACH ROW
BEGIN
    IF (OLD.status is NULL OR OLD.status != NEW.status) THEN
        INSERT INTO orders_status(order_id, status)
        VALUES (NEW.id, NEW.status);

        IF (NEW.status = 'CANCELLED_BY_USER' OR NEW.status = 'CANCELLED_BY_SELLER') THEN
            INSERT INTO orders_status(order_id, status)
            VALUES (NEW.id, 'REFUND_INITIATED');
        END IF;
    END IF;
    IF (OLD.rating IS NULL AND NEW.rating IS NOT NULL) THEN
        CALL shop_rating_update(OLD.shop_id);
    END IF;
END;
$$

####################################################

CREATE INDEX shop_place_id_index
    ON shop (place_id);

CREATE INDEX users_oauth_id_index
    ON users (oauth_id);

CREATE INDEX items_shop_id_index
    ON item (shop_id);

CREATE INDEX items_name_index
    ON item (name);

CREATE INDEX orders_shop_id_index
    ON orders (shop_id);

CREATE INDEX orders_user_id_index
    ON orders (user_id);
