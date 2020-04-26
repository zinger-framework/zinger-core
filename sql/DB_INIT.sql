CREATE DATABASE sfdbgffed;

USE sfdbgffed;

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
    notif_token JSON                                                             NOT NULL,
    role        ENUM ('CUSTOMER','SELLER','SHOP_OWNER','DELIVERY','SUPER_ADMIN') NOT NULL,
    is_delete   INT         DEFAULT 0,
    CONSTRAINT users_id_pk PRIMARY KEY (id)
);

CREATE TABLE users_invite
(
    mobile     VARCHAR(10)                             NOT NULL,
    shop_id    INT                                     NOT NULL,
    invited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role       ENUM ('SELLER','DELIVERY','SHOP_OWNER') NOT NULL,
    is_delete  INT       DEFAULT 0,
    CONSTRAINT users_invite_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
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
    CONSTRAINT item_name_shop_id_pk PRIMARY KEY (name, price, shop_id),
    CONSTRAINT item_id_uq UNIQUE (id),
    CONSTRAINT item_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

CREATE TABLE orders
(
    id                       INT AUTO_INCREMENT,
    user_id                  INT    NOT NULL,
    shop_id                  INT    NOT NULL,
    date                     TIMESTAMP                       DEFAULT CURRENT_TIMESTAMP,
    status                   ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') DEFAULT NULL,
    price                    DOUBLE NOT NULL,
    delivery_price           DOUBLE                          DEFAULT NULL,
    delivery_location        VARCHAR(128)                    DEFAULT NULL,
    cooking_info             VARCHAR(128)                    DEFAULT NULL,
    rating                   DOUBLE(2, 1)                    DEFAULT NULL,
    feedback                 VARCHAR(1024)                   DEFAULT NULL,
    secret_key               VARCHAR(10)                     DEFAULT NULL,
    CONSTRAINT orders_id_pk PRIMARY KEY (id),
    CONSTRAINT orders_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT orders_shop_id_fk FOREIGN KEY (shop_id) REFERENCES shop (id)
);

create table orders_status
(
    order_id     INT NOT NULL,
    status       ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') NOT NULL,
    updated_time DATETIME                                    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT orders_status_order_id_status_pk PRIMARY KEY (order_id, status),
    CONSTRAINT orders_status_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id)
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

####################################################

CREATE TRIGGER new_rating
    AFTER INSERT
    ON shop
    FOR EACH ROW
    INSERT INTO rating(shop_id)
    VALUES (NEW.id);

CREATE TRIGGER seller_archive
    AFTER DELETE
    ON users_shop
    FOR EACH ROW
    INSERT INTO seller_archive(user_id, shop_id)
    VALUES (OLD.user_id, OLD.shop_id);

DELIMITER \\
CREATE TRIGGER order_placed_time
    BEFORE UPDATE
    ON orders
    FOR EACH ROW
    IF (NEW.status LIKE 'PLACED' OR NEW.status LIKE 'PENDING' OR NEW.status LIKE 'TXN_FAILURE') THEN
        SET NEW.date = CURRENT_TIMESTAMP;
    END IF;
\\

####################################################

CREATE INDEX place_is_delete_index
    ON place (is_delete);

CREATE INDEX place_name_index
    ON place (name);

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

####################################################

-- -1 -> order not taken
-- -2 -> delivery not available
-- -3 -> item unavailable
-- 0 or actual_delivery_price

DROP PROCEDURE IF EXISTS getDeliveryPrice;
DROP PROCEDURE IF EXISTS calculatePrice;
DROP PROCEDURE IF EXISTS verifyPricing;

####################################################

DELIMITER $$
CREATE PROCEDURE getDeliveryPrice(
    IN s_id INT,
    IN order_type char,
    OUT d_price INT
)
BEGIN
    DECLARE actual_delivery_price DOUBLE;
    DECLARE actual_is_delivery_available INT;
    DECLARE actual_is_order_taken INT;

    SELECT delivery_price, is_delivery_available, is_order_taken
    into actual_delivery_price, actual_is_delivery_available, actual_is_order_taken
    from configurations
    where shop_id = s_id;

    IF actual_is_order_taken = 0 THEN
        set d_price = -1;
    ELSE
        IF order_type = 'D' THEN
            IF actual_is_delivery_available = 1  THEN
                set d_price = actual_delivery_price;
            ELSE
                set d_price = -2;
            END IF;
        ELSE
            set d_price = 0;
        END IF;
    END IF;

END$$
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE calculatePrice(
    IN item_list json,
    OUT total_price INT
)
BEGIN
    DECLARE item_length BIGINT UNSIGNED DEFAULT JSON_LENGTH(item_list);
    DECLARE item_index BIGINT UNSIGNED DEFAULT 0;
    DECLARE item_id INT DEFAULT 0;
    DECLARE item_quantity INT DEFAULT 0;
    DECLARE item_price INT;
    set total_price = 0;

    item_loop:
    WHILE item_index < item_length DO
            set item_quantity = JSON_EXTRACT(item_list, CONCAT('$[', item_index, '].quantity'));
            set item_id = JSON_EXTRACT(item_list, CONCAT('$[', item_index, '].itemId'));
            set item_price = null;

            select price
            into item_price
            from item
            where item.id = item_id and
                    item.is_available = 1 and
                    item.is_delete = 0;

            IF(item_price is null) THEN
                set total_price = -3;
                LEAVE item_loop;
            end if;

            set total_price = total_price + (item_price * item_quantity);
            SET item_index = item_index + 1;
        END WHILE item_loop;
END$$;
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE verifyPricing(
    IN item_list json,
    IN s_id INT,
    IN order_type char,
    OUT total_price INT
)
BEGIN
    CALL getDeliveryPrice(s_id, order_type, @delivery_price);

    if(@delivery_price < 0) THEN
        SET total_price = @delivery_price;
    ELSE
        CALL calculatePrice(item_list, total_price);

        if(total_price > 0) THEN
            SET total_price = total_price + @delivery_price;
        END IF;
    END IF;
END$$;
DELIMITER ;

####################################################

#CALL verifyPricing('[{"itemId":1,"quantity":1},{"itemId":2,"quantity":2}]', 1, 'P', @total_price);
#select @total_price;

####################################################

