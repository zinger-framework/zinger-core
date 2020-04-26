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

####################################################
# Get Order By userId pagination

select o.id, o.date, o.status, o.price, o.delivery_price, o.delivery_location, o.cooking_info, o.rating, o.feedback, o.secret_key,
 t.transaction_id, t.payment_mode,
 s.name as shop_name, s.photo_url, s.mobile as shop_mobile,
 group_concat(i.name) as item_name, group_concat(i.price) as item_price, group_concat(i.is_veg) as item_is_veg,
 group_concat(oi.quantity) as order_item_quantity, group_concat(oi.price) as order_item_price
from orders as o
inner join transactions as t on
o.id = t.order_id and
o.user_id = 2
inner join orders_item as oi on
oi.order_id = o.id
inner join item as i on
i.id = oi.item_id
inner join shop as s on
s.id = o.shop_id
group by o.id, t.transaction_id
order by o.date desc
limit 5 offset 0;

####################################################
# Get Order By shopId pagination


