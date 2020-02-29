use food;

INSERT INTO users (name, email, mobile, oauth_id,role) VALUES('vishnu','vishnu@gmail.com',9493984452,'abcd','CUSTOMER');
select * from users;

INSERT INTO college (name,icon_url,address) VALUES('SSN College of Engineering','https://www.ssn.net','Chennai');
select * from college;

select * from college where address like "%che%";

INSERT INTO shop(name,photo_url,mobile,college_id,opening_time,closing_time,configuration_id) VALUES('Snowcube','https://www.snowcube.net',9012503040,2,'09:00:00','18:00:00',1);
select * from shop;

INSERT INTO item(name,price,photo_url,category,shop_id,is_veg,is_available) VALUES('ChocolateMilkshake',80,'https://www.milkshake.net','Shakes',7,0,0);
select * from item;

select * from item WHERE shop_id = 4 AND name LIKE "Milkshake";

select * from item WHERE name LIKE "%ilk%";

select * from item WHERE name LIKE '%en%';

select * from users_college;


select * from users_shop;

INSERT INTO configurations(id,delivery_price,is_delivery_available,is_order_taken) VALUES(1,70.0,0,0);
select * from configurations;