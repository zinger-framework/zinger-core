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
select * from orders_status;
select * from rating;
select * from configurations;

####################################################
# Get Order By userId pagination

select o.id, o.date, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 o.transaction_id, o.payment_mode,
 o.shop_name,o.photo_url,o.shop_mobile,
 o.item_name,o.item_price,o.is_veg,
 o.quantity,o.order_item_price,
 y.status,y.updated_time
 from
(select o.*,
 t.transaction_id, t.payment_mode,
 s.name as shop_name, s.photo_url, s.mobile as shop_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as is_veg,
 group_concat(oi.quantity) as quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.user_id = 2
INNER join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join shop as s on
s.id = o.shop_id
group by o.id, t.transaction_id
order by o.date desc
limit 5 offset 0) as o,

(select o.id, group_concat(os.status) as status, group_concat(os.updated_time) as updated_time
from orders as o
inner join orders_status as os on
os.order_id = o.id and
o.user_id = 2
group by o.id
order by o.date desc
limit 5 offset 0) as y
where o.id = y.id;

####################################################
# Get Order By shopId pagination

select o.id, o.date, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 o.transaction_id, o.payment_mode,
 o.user_name,o.user_mobile,
 o.item_name,o.item_price,o.is_veg,
 o.quantity,o.order_item_price,
 y.status,y.updated_time
 from
(select o.*,
 t.transaction_id, t.payment_mode,
 u.name as user_name, u.mobile as user_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as is_veg,
 group_concat(oi.quantity) as quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.shop_id = 1 and
o.status in ('CANCELLED_BY_SELLER','CANCELLED_BY_USER','COMPLETED','REFUND_INITIATED','REFUND_COMPLETED','DELIVERED')
INNER join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join users as u on
u.id = o.user_id
group by o.id, t.transaction_id
order by o.date desc
limit 5 offset 0) as o,
(select o.id, group_concat(os.status) as status, group_concat(os.updated_time) as updated_time
from orders as o
inner join orders_status as os on
os.order_id = o.id and
o.shop_id = 1 and
o.status in ('CANCELLED_BY_SELLER','CANCELLED_BY_USER','COMPLETED','REFUND_INITIATED','REFUND_COMPLETED','DELIVERED')
group by o.id
order by o.date desc
limit 5 offset 0) as y
where o.id = y.id;

####################################################
# Get Order By shopId dashboard

select o.id, o.date, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 o.transaction_id, o.payment_mode,
 o.user_name,o.user_mobile,
 o.item_name,o.item_price,o.is_veg,
 o.quantity,o.order_item_price,
 y.status,y.updated_time
 from
(select o.*,
 t.transaction_id, t.payment_mode,
 u.name as user_name, u.mobile as user_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as is_veg,
 group_concat(oi.quantity) as quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.shop_id = 1 and
o.status in ('PLACED','ACCEPTED','READY','OUT_FOR_DELIVERY')
INNER join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join users as u on
u.id = o.user_id
group by o.id, t.transaction_id
order by o.date) as o,
(select o.id, group_concat(os.status) as status, group_concat(os.updated_time) as updated_time
from orders as o
inner join orders_status as os on
os.order_id = o.id and
o.shop_id = 1 and
o.status in ('PLACED','ACCEPTED','READY','OUT_FOR_DELIVERY')
group by o.id
order by o.date) as y
where o.id = y.id;

####################################################
# Get Order By shopId Filter

select o.id, o.date, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 o.transaction_id, o.payment_mode,
 o.user_name,o.user_mobile,
 o.item_name,o.item_price,o.is_veg,
 o.quantity,o.order_item_price,
 y.status,y.updated_time
 from
(select o.*,
 t.transaction_id, t.payment_mode,
 u.name as user_name, u.mobile as user_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as is_veg,
 group_concat(oi.quantity) as quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.shop_id = 1 and
o.status in ('CANCELLED_BY_SELLER','CANCELLED_BY_USER','COMPLETED','REFUND_INITIATED','REFUND_COMPLETED','DELIVERED')
INNER join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join users as u on
u.id = o.user_id
where o.id LIKE '%1%' or u.name LIKE '%2%'
group by o.id, t.transaction_id
order by o.date desc
limit 5 offset 0) as o,

(select o.id, group_concat(os.status) as status, group_concat(os.updated_time) as updated_time
from orders as o
inner join orders_status as os on
os.order_id = o.id and
o.shop_id = 1 and
o.status in ('CANCELLED_BY_SELLER','CANCELLED_BY_USER','COMPLETED','REFUND_INITIATED','REFUND_COMPLETED','DELIVERED')
inner join users as u on
u.id = o.user_id
where o.id LIKE '%1%' or u.name LIKE '%log%'
group by o.id
order by o.date desc
limit 5 offset 0) as y
where o.id = y.id;

####################################################
# Get Order By Id

select o.id, o.date, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 o.transaction_id, o.payment_mode,
 o.user_name,o.user_mobile,
 o.shop_name,o.photo_url,o.shop_mobile,
 o.item_name,o.item_price,o.is_veg,
 o.quantity,o.order_item_price,
 y.status,y.updated_time
 from
(select o.*,
 t.transaction_id, t.payment_mode,
 u.name as user_name, u.mobile as user_mobile,
 s.name as shop_name, s.photo_url, s.mobile as shop_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as is_veg,
 group_concat(oi.quantity) as quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.id = 2
INNER join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join users as u on
u.id = o.user_id
inner join shop as s on
s.id = o.shop_id
group by o.id, t.transaction_id) as o,

(select o.id, group_concat(os.status) as status, group_concat(os.updated_time) as updated_time
from orders as o
inner join orders_status as os on
os.order_id = o.id and
o.id = 2
group by o.id) as y
where o.id = y.id;

####################################################
# Testing

