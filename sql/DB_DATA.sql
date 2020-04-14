insert into place values(1,'SSN Place of Enginering','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','kelambakkam',0);
insert into place values(2,'VIT University','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','vandaloor',0);
insert into place values(3,'SRM University','https://www.foodiesfeed.com/wp-content/uploads/2019/04/mae-mu-oranges-ice-349x436.jpg','ramapuram',0);

insert into shop values(1,'sathyas main','shop.com','["shop.com","shop.com","shop.com"]',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(2,'Snow Qube','shop.com','["shop.com","shop.com","shop.com"]',9176019344,1,'09:00:00','21:00:00',0);
insert into shop values(3,'sathyas small','shop.com','["shop.com","shop.com","shop.com"]',9176019344,1,'09:00:00','21:00:00',0);

insert into shop values(4,'VIT sathyas main','shop.com','["shop.com","shop.com","shop.com"]',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(5,'VIT Snow Qube','shop.com','["shop.com","shop.com","shop.com"]',9176019344,2,'09:00:00','21:00:00',0);
insert into shop values(6,'VIT sathyas small','shop.com','["shop.com","shop.com","shop.com"]',9176019344,2,'09:00:00','21:00:00',0);

insert into shop values(7,'SRM sathyas main','shop.com','["shop.com","shop.com","shop.com"]',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(8,'SRM Snow Qube','shop.com','["shop.com","shop.com","shop.com"]',9176019344,3,'09:00:00','21:00:00',0);
insert into shop values(9,'SRM sathyas small','shop.com','["shop.com","shop.com","shop.com"]',9176019344,3,'09:00:00','21:00:00',0);

-- insert into users values(9789075309,'Abhiram','abhi@yahoo.co.in','24oauthid12','SHOP_OWNER',0);
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

insert into configurations(shop_id, delivery_price, merchant_id) values(1, 15.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(2, 10.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(3, 25.0, 'MID');

insert into configurations(shop_id, delivery_price, merchant_id) values(4, 15.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(5, 10.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(6, 25.0, 'MID');

insert into configurations(shop_id, delivery_price, merchant_id) values(7, 15.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(8, 10.0, 'MID');
insert into configurations(shop_id, delivery_price, merchant_id) values(9, 25.0, 'MID');

insert into users_shop values('9176786583',1);

insert into transactions (transaction_id,order_id,bank_transaction_id,currency,response_code,response_message,gateway_name,bank_name,
payment_mode,checksum_hash) values("1","O00011","dsfs","RS","01","success","STRIPE","DBS","UPI","3232");

insert into transactions (transaction_id,order_id,bank_transaction_id,currency,response_code,response_message,gateway_name,bank_name,
payment_mode,checksum_hash) values("2","2","dsfs","RS","01","success","STRIPE","DBS","UPI","3232");

insert into transactions (transaction_id,order_id,bank_transaction_id,currency,response_code,response_message,gateway_name,bank_name,
payment_mode,checksum_hash) values("3","O00012","dsfs","RS","01","success","STRIPE","DBS","UPI","3232");


select * from users;
-- select * from place;
-- select * from place_log;
select * from shop;
select * from item;
-- select * from item_log;
-- select * from users_log;
select * from users_place;
select * from users_shop;
select * from users_invite;
-- select * from transactions;
select * from orders;
select * from orders_item;
-- select * from rating;
select * from configurations;

