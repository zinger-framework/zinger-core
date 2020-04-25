package com.food.ordering.zinger.constant;

public class ApiConfig {
    public static final String EMPTY_QUOTES = "";

    public static class PlaceApi {
        public static final String BASE_URL = "/place";
        public static final String insertPlace = EMPTY_QUOTES;
        public static final String getAllPlaces = EMPTY_QUOTES;
    }

    public static class ItemApi {
        public static final String BASE_URL = "/menu";

        public static final String insertItem = EMPTY_QUOTES;
        public static final String getItemsByShopId = "/shop/{shopId}";
        public static final String getItemsByName = "/{placeId}/{itemName}";
        public static final String updateItem = EMPTY_QUOTES;
        public static final String deleteItemById = "/delete/{itemId}";
    }

    public static class OrderApi {
        public static final String BASE_URL = "/order";
        public static final String insertOrder = EMPTY_QUOTES;
        public static final String placeOrder = "/place/{orderId}";
        public static final String getOrderByUserId = "/customer/{userId}/{pageNum}/{pageCount}";
        public static final String getOrderByShopIdPagination = "/seller/{shopId}/{pageNum}/{pageCount}";
        public static final String getOrderByShopId = "/seller/{shopId}";
        public static final String getOrderById = "/{id}";
        public static final String updateOrderRating = "/rating";
        public static final String updateOrderStatus = "/status";
    }

    public static class UserApi {
        public static final String BASE_URL = "/user";
        public static final String loginRegisterCustomer = "/customer";
        public static final String verifySeller = "/seller";
        public static final String inviteSeller = "/seller/invite";
        public static final String verifyInvite = "/verify/invite/{shopId}/{mobile}";
        public static final String acceptInvite = "/accept/invite";
        public static final String deleteInvite = "/seller/invite";
        public static final String getSellerByShopId = "/seller/{shopId}";
        public static final String updateUser = EMPTY_QUOTES;
        public static final String updateUserPlaceData = "/place";
        public static final String deleteSeller = "/seller/{shopId}/{userId}";
    }

    public static class NotifyApi {
        public static final String BASE_URL = "/notify";
        public static final String inviteSeller = "/seller/invite";
    }

    public static class ShopApi {
        public static final String BASE_URL = "/shop";
        public static final String insertShop = EMPTY_QUOTES;
        public static final String getShopsByPlaceId = "/place/{placeId}";
        public static final String getShopById = "/{shopId}";
        public static final String updateShopConfiguration = "/config";
        public static final String deleteShop = "/{shopId}";
    }
}
