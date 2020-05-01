####################################################

# -1 -> order not taken
# -2 -> delivery not available
# -3 -> item unavailable
# -4 -> secret key mismatch
#  0 (or) actual_delivery_price

DROP PROCEDURE IF EXISTS get_delivery_price;
DROP PROCEDURE IF EXISTS calculate_price;
DROP PROCEDURE IF EXISTS verify_pricing;
DROP PROCEDURE IF EXISTS validate_order_status;
DROP PROCEDURE IF EXISTS order_status_update;
DROP PROCEDURE IF EXISTS shop_rating_update;

####################################################

DELIMITER $$
CREATE PROCEDURE get_delivery_price(
    IN s_id INT,
    IN order_type char,
    OUT d_price INT,
    OUT m_id VARCHAR(32)
)
BEGIN
    DECLARE actual_delivery_price DOUBLE;
    DECLARE actual_is_delivery_available INT;
    DECLARE actual_is_order_taken INT;
    DECLARE actual_m_id varchar(32) DEFAULT NULL;

    SELECT delivery_price, is_delivery_available, is_order_taken,merchant_id
    into actual_delivery_price, actual_is_delivery_available, actual_is_order_taken,actual_m_id
    from configurations
    where shop_id = s_id;

    IF actual_is_order_taken = 0 THEN
        set d_price = -1;
    ELSE
        IF order_type = 'D' THEN
            IF actual_is_delivery_available = 1  THEN
                set d_price = actual_delivery_price;
                set m_id = actual_m_id;
            ELSE
                set d_price = -2;
            END IF;
        ELSE
            set d_price = 0;
            set m_id = actual_m_id;
        END IF;
    END IF;

END$$
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE calculate_price(
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
CREATE PROCEDURE verify_pricing(
    IN item_list json,
    IN s_id INT,
    IN order_type char,
    OUT total_price INT,
    OUT m_id varchar(32)
)
BEGIN
    CALL get_delivery_price(s_id, order_type, @delivery_price, @merchant_id);

    if(@delivery_price < 0) THEN
        SET total_price = @delivery_price;
    ELSE
        CALL calculate_price(item_list, total_price);

        if(total_price > 0) THEN
            SET total_price = total_price + @delivery_price;
            SET m_id = @merchant_id;
        END IF;
    END IF;
END$$;
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE validate_order_status(
    IN o_id INT,
    IN new_status ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED'),
	IN new_secret_key VARCHAR(10),
    OUT result INT
)
BEGIN
    DECLARE actual_status ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED') DEFAULT NULL;
    DECLARE actual_delivery_location VARCHAR(128) DEFAULT NULL;
    DECLARE actual_secret_key VARCHAR(10) DEFAULT NULL;
    SET result = 0;

    SELECT status, delivery_location, secret_key
    INTO actual_status, actual_delivery_location, actual_secret_key
    from orders
    where id = o_id;

	case actual_status
		when 'PENDING' THEN
			set result = (new_status = 'TXN_FAILURE') OR (new_status = 'PLACED') OR (new_status = 'REFUND_INITIATED');
		when 'PLACED' THEN
			set result = (new_status = 'CANCELLED_BY_SELLER') OR (new_status = 'CANCELLED_BY_USER') OR (new_status = 'ACCEPTED');
		when 'ACCEPTED' THEN
			IF actual_delivery_location IS NULL THEN
				set result = (new_status = 'READY') OR (new_status = 'CANCELLED_BY_SELLER');
			ELSE
				set result = (new_status = 'OUT_FOR_DELIVERY') OR (new_status = 'CANCELLED_BY_SELLER');
			END IF;
		when 'READY' THEN
			IF new_status = 'COMPLETED' THEN
				IF actual_secret_key = new_secret_key THEN
					set result = 1;
				ELSE
					set result = -4;
				END IF;
			END IF;
		when 'OUT_FOR_DELIVERY' THEN
			IF new_status = 'DELIVERED' THEN
				IF actual_secret_key = new_secret_key THEN
					set result = 1;
				ELSE
					set result = -4;
				END IF;
			END IF;
		ELSE
			IF actual_status = 'CANCELLED_BY_USER' OR actual_status = 'CANCELLED_BY_SELLER' OR actual_status = 'REFUND_INITIATED' THEN
				set result = (new_status = 'REFUND_COMPLETED');
			ELSEIF actual_status IS NULL THEN
				set result = (new_status = 'TXN_FAILURE') OR (new_status = 'PENDING') OR (new_status = 'PLACED');
			END IF;
    END CASE;
END$$
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE order_status_update(
    IN o_id INT,
    IN new_status ENUM ('PENDING', 'TXN_FAILURE', 'PLACED',
        'CANCELLED_BY_USER', 'ACCEPTED', 'CANCELLED_BY_SELLER',
        'READY', 'OUT_FOR_DELIVERY', 'COMPLETED',
        'DELIVERED', 'REFUND_INITIATED', 'REFUND_COMPLETED'),
	IN new_secret_key VARCHAR(10),
    OUT result INT
)
BEGIN
	DECLARE gen_secret_key VARCHAR(10) DEFAULT NULL;
    CALL validate_order_status(o_id, new_status, new_secret_key, result);
    IF result = 1 THEN
		IF new_status = 'READY' OR new_status = 'OUT_FOR_DELIVERY' THEN
            SET new_secret_key = LPAD(FLOOR(RAND() * 999999.99), 6, '0');
            UPDATE orders
			set secret_key = new_secret_key, status = new_status
			where id = o_id;
		else
			UPDATE orders
			set status = new_status
			where id = o_id;
        END IF;
    END IF;
END$$
DELIMITER ;

####################################################

DELIMITER $$
CREATE PROCEDURE shop_rating_update(
    IN s_id INT
)
BEGIN
	DECLARE actual_rating DOUBLE(2, 1) DEFAULT NULL;
	DECLARE actual_user_count INT DEFAULT NULL;
    
    SELECT COUNT(rating), AVG(rating)
    INTO actual_user_count, actual_rating
    FROM orders
    WHERE shop_id = s_id AND
    rating IS NOT NULL;

    UPDATE rating
    SET rating = actual_rating,
    user_count = actual_user_count
    WHERE shop_id = s_id;
END$$
DELIMITER ;

####################################################

# CALL verify_pricing('[{"itemId":1,"quantity":1},{"itemId":2,"quantity":2}]', 1, 'P', @total_price, @m_id);
# select @total_price, @m_id;

# CALL order_status_update(1, 'DELIVERED', '966318', @result);
# SELECT @result;

# CALL shop_rating_update(1);

####################################################
