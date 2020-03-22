insert into college values(1,'SSN College of Enginering','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Kelambakkam',0);
insert into college values(2,'VIT University','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Vandaloor',0);
insert into college values(3,'SRM University','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Ramapuram',0);

insert into shop values(1,'Sathyas Main Canteen','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(2,'Snow Qube','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(3,'Viswaas','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(4,'Sathyas Fast Food','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg',9176019344,1,'09:00:00','21:00:00',0);

insert into item values(1,'Chicken Fried Rice',80,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Chinese',1,0,1,0);
insert into item values(2,'Chicken Noodles',90,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Chinese',1,0,1,0);
insert into item values(3,'Veg Noodles',60,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','Chinese',1,1,1,0);
insert into item values(4,'Masala Dosa',45,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','South Indian',1,1,1,0);
insert into item values(5,'Sada Dosa',35,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','South Indian',1,1,1,0);
insert into item values(6,'Onion Dosa',50,'https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','South Indian',1,1,1,0);

insert into configurations(shop_id, delivery_price) values(1,5.0);
insert into configurations(shop_id, delivery_price) values(2,15.0);
insert into configurations(shop_id, delivery_price) values(3,20.0);
insert into configurations(shop_id, delivery_price) values(4,25.0);

select * from users;
select * from college;
select * from college_log;
select * from shop;
select * from item;
select * from users_college;
select * from users_shop;
select * from transactions;
select * from orders;
select * from orders_item;
select * from rating;
select * from configurations;

SELECT oauth_id, name, email, mobile, role, is_delete FROM users WHERE mobile = '9176786583' AND role = 'SELLER' AND is_delete = 0;
SELECT mobile, shop_id FROM users_shop WHERE mobile = '9176786583';
SELECT id, name, photo_url, photo_url, college_id, opening_time, closing_time, is_delete FROM shop WHERE id = 1;

SELECT id, mobile, transaction_id, shop_id, date, status, last_status_updated_time, price, delivery_price, delivery_location, cooking_info, rating, secret_key FROM orders WHERE shop_id = 2 AND (status = 'PLACED' || status = 'ACCEPTED' || status = 'READY' || status = 'OUT_FOR_DELIVERY') ORDER BY date DESC;

SELECT id, mobile, transaction_id, shop_id, date, status, last_status_updated_time, price, delivery_price, delivery_location, cooking_info, rating, secret_key FROM orders WHERE mobile = '9176786580' ORDER BY date DESC LIMIT 2 OFFSET 2;
SELECT transaction_id, bank_transaction_id, currency, response_code, response_message, gateway_name, bank_name, payment_mode, checksum_hash FROM transactions WHERE transaction_id = 'T0013';

SELECT oauth_id, name, email, mobile, role, is_delete FROM users WHERE mobile = '9176786581' AND role = 'SELLER' AND is_delete = 0;
SELECT id FROM shop WHERE college_id = 2 AND is_delete = 0;
SELECT id, mobile, transaction_id, shop_id, date, status, last_status_updated_time, price, delivery_price, delivery_location, cooking_info, rating, secret_key FROM orders WHERE id = 'O0005';
SELECT id, name, price, photo_url, category, shop_id, is_veg, is_available, is_delete FROM item WHERE name LIKE '%pa%' AND is_delete = 0 AND shop_id IN (SELECT id FROM shop WHERE college_id = 2 AND is_delete = 0);
