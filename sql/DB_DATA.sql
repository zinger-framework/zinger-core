insert into college values(1,'SSN','test.com','kelambakkam',0);
insert into college values(2,'VIT','test.com','vandaloor',0);
insert into college values(3,'SRM','test.com','ramapuram',0);

insert into shop values(1,'sathyas main','shop.com',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(2,'Snow Qube','shop.com',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(3,'sathyas small','shop.com',9176019344,1,'09:00:00','21:00:00',0);

insert into shop values(4,'VIT sathyas main','shop.com',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(5,'VIT Snow Qube','shop.com',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(6,'VIT sathyas small','shop.com',9176019344,2,'09:00:00','21:00:00',0);

insert into shop values(7,'SRM sathyas main','shop.com',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(8,'SRM Snow Qube','shop.com',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(9,'SRM sathyas small','shop.com',9176019344,3,'09:00:00','21:00:00',0);

insert into users values(9176786581,'Harshavardhan','harshavardhan98@yahoo.co.in','auth_9176786581','SELLER',0);
insert into users values(9176019345,'logesh','harshavardhan98@yahoo.co.in','12oauthid35','SELLER',0);
insert into users values(9176019346,'shrikanth','harshavardhan98@yahoo.co.in','12oauthid36','SHOP_OWNER',0);

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
insert into configurations(shop_id, delivery_price) values(1,5.0);

insert into users_college values("9176786582",1);
insert into users_shop values("9176786581",1);
insert into users_shop values("9176786581",2);

select * from users;
select * from college;
select * from shop;
select * from item;
select * from users_college;
select * from users_shop;
select * from transactions;
select * from orders;
select * from orders_item;

SELECT oauth_id, name, email, mobile, role, is_delete FROM users WHERE mobile = '9176786583' AND role = 'SELLER' AND is_delete = 0;
SELECT mobile, shop_id FROM users_shop WHERE mobile = '9176786583';
SELECT id, name, photo_url, photo_url, college_id, opening_time, closing_time, is_delete FROM shop WHERE id = 1;

SELECT oauth_id, name, email, mobile, role, is_delete FROM users WHERE mobile = '9176786581' AND role = 'SELLER' AND is_delete = 0;
SELECT id FROM shop WHERE college_id = 2 AND is_delete = 0;
SELECT id, mobile, transaction_id, shop_id, date, status, last_status_updated_time, price, delivery_price, delivery_location, cooking_info, rating, secret_key FROM orders WHERE id = 'O0005';
SELECT id, name, price, photo_url, category, shop_id, is_veg, is_available, is_delete FROM item WHERE name LIKE '%pa%' AND is_delete = 0 AND shop_id IN (SELECT id FROM shop WHERE college_id = 2 AND is_delete = 0);