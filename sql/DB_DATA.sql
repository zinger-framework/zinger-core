USE sfdbgffed;

select * from users;
select * from place;
select * from place_log;
select * from shop;
select * from item;
select * from users_place;
select * from users_shop;
select * from users_invite;
select * from transactions;
select * from orders;
select * from orders_item;
select * from rating;
select * from configurations;

CALL verifyPricing('[{"itemId":1,"quantity":1},{"itemId":2,"quantity":2}]', 1, 'P', @total_price);
select @total_price;

select o.id,t.id,oi.*
from orders as o
inner join
transactions as t
on o.id=t.order_id and o.user_id=3
inner join
orders_item as oi
on oi.order_id=o.id;
