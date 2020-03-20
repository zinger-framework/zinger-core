insert into college values(1,'SSN','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg
','kelambakkam',0);
insert into college values(2,'VIT','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg
','vandaloor',0);
insert into college values(3,'SRM','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg
','ramapuram',0);

insert into shop values(1,'sathyas main','shop.com',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(2,'Snow Qube','shop.com',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(3,'sathyas small','shop.com',9176019344,1,'09:00:00','21:00:00',0);

insert into shop values(4,'VIT sathyas main','shop.com',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(5,'VIT Snow Qube','shop.com',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(6,'VIT sathyas small','shop.com',9176019344,2,'09:00:00','21:00:00',0);

insert into shop values(7,'SRM sathyas main','shop.com',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(8,'SRM Snow Qube','shop.com',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(9,'SRM sathyas small','shop.com',9176019344,3,'09:00:00','21:00:00',0);

insert into users values(9789075309,'Abhiram','abhi@yahoo.co.in','24oauthid12','SHOP_OWNER',0);
-- insert into users values(9176019346,'shrikanth','harshavardhan98@yahoo.co.in','12oauthid36','SHOP_OWNER',0);
-- insert into users values(9176019347,'spider-man','harshavardhan98@yahoo.co.in','12oauthid37','CUSTOMER',0);
-- insert into users values(9176019348,'Harshavardhan','harshavardhan98@yahoo.co.in','12oauthid38','SELLER',0);

insert into item values(1,'chicken fried rice',75,'food.com','fast food',1,0,0,0);
insert into item values(2,'chicken noodles',75,'food.com','fast food',1,0,0,0);
insert into item values(3,'dosa',75,'food.com','fast food',2,0,0,0);
insert into item values(4,'idly',75,'food.com','fast food',2,0,0,0);
insert into item values(5,'burger',75,'food.com','fast food',3,0,0,0);
insert into item values(6,'burger',75,'food.com','fast food',3,0,0,0);
insert into item values(7,'parota',75,'food.com','fast food',4,0,0,0);
insert into item values(8,'chappati',75,'food.com','fast food',5,0,0,0);
insert into item values(9,'samosa',75,'food.com','fast food',6,0,0,0);
insert into item values(10,'channa samosa',75,'food.com','fast food',7,0,0,0);
insert into item values(11,'chicken 65',75,'food.com','fast food',8,0,0,0);
insert into item values(12,'biriyani',75,'food.com','fast food',9,0,0,0);

insert into rating values(1,2.8,23);
insert into rating values(2,4.8,5);
insert into rating values(3,3.7,98);

insert into configurations(shop_id, delivery_price) values(1,5.0);
insert into configurations(shop_id, delivery_price) values(2,15.0);
insert into configurations(shop_id, delivery_price) values(3,20.0);

-- insert into users_college values("9176786582",1);
-- insert into users_shop values("9176786581",1);
-- insert into users_shop values("9176786581",2);

ALTER table college 
MODIFY icon_url varchar(128) NOT NULL;

select * from users;
select * from college;
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