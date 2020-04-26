package com.food.ordering.zinger.constant;

public class Column {
    public static final String placeAddress = "place_address";
    public static final String placeName = "place_name";

    public static final String shopName = "shop_name";
    public static final String shopMobile = "shop_mobile";

    public static final String userName = "user_name";
    public static final String userMobile = "user_mobile";

    public static final String itemName = "item_name";
    public static final String itemPrice = "item_price";

    public static final String orderItemPrice = "order_item_price";
    public static final String searchQuery = "search_query";

    public static final class PlaceColumn {
        public static final String tableName = "place";

        public static final String id = "id";
        public static final String name = "name";
        public static final String iconUrl = "icon_url";
        public static final String address = "address";
        public static final String isDelete = "is_delete";
    }

    public static final class ConfigurationColumn {
        public static final String tableName = "configurations";

        public static final String shopId = "shop_id";
        public static final String merchantId = "merchant_id";
        public static final String deliveryPrice = "delivery_price";
        public static final String isDeliveryAvailable = "is_delivery_available";
        public static final String isOrderTaken = "is_order_taken";
    }

    public static final class ItemColumn {
        public static final String tableName = "item";

        public static final String id = "id";
        public static final String name = "name";
        public static final String price = "price";
        public static final String photoUrl = "photo_url";
        public static final String category = "category";
        public static final String shopId = "shop_id";
        public static final String isVeg = "is_veg";
        public static final String isAvailable = "is_available";
        public static final String isDelete = "is_delete";
    }

    public static final class OrderColumn {
        public static final String tableName = "orders";

        public static final String id = "id";
        public static final String userId = "user_id";
        public static final String shopId = "shop_id";
        public static final String date = "date";
        public static final String status = "status";
        public static final String price = "price";
        public static final String deliveryPrice = "delivery_price";
        public static final String deliveryLocation = "delivery_location";
        public static final String cookingInfo = "cooking_info";
        public static final String rating = "rating";
        public static final String feedback = "feedback";
        public static final String secretKey = "secret_key";
    }

    public static final class OrderStatusColumn {
        public static final String tableName = "orders_status";

        public static final String orderId = "order_id";
        public static final String status = "status";
        public static final String updatedTime = "updated_time";
    }

    public static final class OrderItemColumn {
        public static final String tableName = "orders_item";

        public static final String orderId = "order_id";
        public static final String itemId = "item_id";
        public static final String quantity = "quantity";
        public static final String price = "price";
    }

    public static final class RatingColumn {
        public static final String tableName = "rating";

        public static final String shopId = "shop_id";
        public static final String rating = "rating";
        public static final String userCount = "user_count";
    }

    public static final class UserInviteColumn {
        public static final String tableName = "users_invite";

        public static final String mobile = "mobile";
        public static final String shopId = "shop_id";
        public static final String invitedAt = "invited_at";
        public static final String role = "role";
        public static final String isDelete = "is_delete";
    }

    public static final class ShopColumn {
        public static final String tableName = "shop";

        public static final String id = "id";
        public static final String name = "name";
        public static final String photoUrl = "photo_url";
        public static final String coverUrls = "cover_urls";
        public static final String mobile = "mobile";
        public static final String placeId = "place_id";
        public static final String openingTime = "opening_time";
        public static final String closingTime = "closing_time";
        public static final String isDelete = "is_delete";
    }

    public static final class TransactionColumn {
        public static final String tableName = "transactions";

        public static final String transactionId = "transaction_id";
        public static final String orderId = "order_id";
        public static final String bankTransactionId = "bank_transaction_id";
        public static final String currency = "currency";
        public static final String responseCode = "response_code";
        public static final String responseMessage = "response_message";
        public static final String gatewayName = "gateway_name";
        public static final String bankName = "bank_name";
        public static final String paymentMode = "payment_mode";
        public static final String checksumHash = "checksum_hash";
        public static final String date = "date";
    }

    public static final class UserPlaceColumn {
        public static final String tableName = "users_place";

        public static final String userId = "user_id";
        public static final String placeId = "place_id";
    }

    public static final class UserColumn {
        public static final String tableName = "users";

        public static final String id = "id";
        public static final String mobile = "mobile";
        public static final String name = "name";
        public static final String email = "email";
        public static final String oauthId = "oauth_id";
        public static final String notifToken = "notif_token";
        public static final String role = "role";
        public static final String isDelete = "is_delete";
    }

    public static final class UserShopColumn {
        public static final String tableName = "users_shop";

        public static final String userId = "user_id";
        public static final String shopId = "shop_id";
    }

    /**************************************************************/

    public static final class PlaceLogColumn {
        public static final String tableName = "place_log";

        public static final String id = "id";
        public static final String errorCode = "error_code";
        public static final String message = "message";
        public static final String updatedValue = "updated_value";
        public static final String date = "date";
        public static final String priority = "priority";
    }

    public static final class ItemLogColumn {
        public static final String tableName = "item_log";

        public static final String id = "id";
        public static final String errorCode = "error_code";
        public static final String message = "message";
        public static final String updatedValue = "updated_value";
        public static final String date = "date";
        public static final String priority = "priority";
    }

    public static final class OrderLogColumn {
        public static final String tableName = "orders_log";

        public static final String id = "id";
        public static final String errorCode = "error_code";
        public static final String message = "message";
        public static final String updatedValue = "updated_value";
        public static final String date = "date";
        public static final String priority = "priority";
    }

    public static final class ShopLogColumn {
        public static final String tableName = "shop_log";

        public static final String id = "id";
        public static final String errorCode = "error_code";
        public static final String message = "message";
        public static final String updatedValue = "updated_value";
        public static final String date = "date";
        public static final String priority = "priority";
    }

    public static final class UserLogColumn {
        public static final String tableName = "users_log";

        public static final String id = "id";
        public static final String errorCode = "error_code";
        public static final String message = "message";
        public static final String updatedValue = "updated_value";
        public static final String date = "date";
        public static final String priority = "priority";
    }
}
