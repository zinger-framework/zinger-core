package com.food.ordering.zinger.constant;

import com.food.ordering.zinger.constant.Column.*;
import com.food.ordering.zinger.constant.Enums.OrderStatus;

import static com.food.ordering.zinger.constant.Enums.UserRole.SELLER;
import static com.food.ordering.zinger.constant.Sql.*;

public class Query {
    public static final class AuditLogQuery {
        public static final String insertPlaceLog = INSERT_INTO + PlaceLogColumn.tableName + LEFT_PARANTHESIS +
                PlaceLogColumn.id + COMMA +
                PlaceLogColumn.errorCode + COMMA +
                PlaceLogColumn.mobile + COMMA +
                PlaceLogColumn.message + COMMA +
                PlaceLogColumn.updatedValue + COMMA +
                PlaceLogColumn.priority + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + PlaceLogColumn.id +
                COMMA_COLON + PlaceLogColumn.errorCode +
                COMMA_COLON + PlaceLogColumn.mobile +
                COMMA_COLON + PlaceLogColumn.message +
                COMMA_COLON + PlaceLogColumn.updatedValue +
                COMMA_COLON + PlaceLogColumn.priority + RIGHT_PARANTHESIS;

        public static final String insertShopLog = INSERT_INTO + ShopLogColumn.tableName + LEFT_PARANTHESIS +
                ShopLogColumn.id + COMMA +
                ShopLogColumn.errorCode + COMMA +
                ShopLogColumn.mobile + COMMA +
                ShopLogColumn.message + COMMA +
                ShopLogColumn.updatedValue + COMMA +
                ShopLogColumn.priority + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + ShopLogColumn.id +
                COMMA_COLON + ShopLogColumn.errorCode +
                COMMA_COLON + ShopLogColumn.mobile +
                COMMA_COLON + ShopLogColumn.message +
                COMMA_COLON + ShopLogColumn.updatedValue +
                COMMA_COLON + ShopLogColumn.priority + RIGHT_PARANTHESIS;

        public static final String insertUserLog = INSERT_INTO + UserLogColumn.tableName + LEFT_PARANTHESIS +
                UserLogColumn.usersMobile + COMMA +
                UserLogColumn.errorCode + COMMA +
                UserLogColumn.mobile + COMMA +
                UserLogColumn.message + COMMA +
                UserLogColumn.updatedValue + COMMA +
                UserLogColumn.priority + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + UserLogColumn.usersMobile +
                COMMA_COLON + UserLogColumn.errorCode +
                COMMA_COLON + UserLogColumn.mobile +
                COMMA_COLON + UserLogColumn.message +
                COMMA_COLON + UserLogColumn.updatedValue +
                COMMA_COLON + UserLogColumn.priority + RIGHT_PARANTHESIS;

        public static final String insertItemLog = INSERT_INTO + ItemLogColumn.tableName + LEFT_PARANTHESIS +
                ItemLogColumn.id + COMMA +
                ItemLogColumn.errorCode + COMMA +
                ItemLogColumn.mobile + COMMA +
                ItemLogColumn.message + COMMA +
                ItemLogColumn.updatedValue + COMMA +
                ItemLogColumn.priority + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + ItemLogColumn.id + COMMA_COLON +
                ItemLogColumn.errorCode + COMMA_COLON +
                ItemLogColumn.mobile + COMMA_COLON +
                ItemLogColumn.message + COMMA_COLON +
                ItemLogColumn.updatedValue + COMMA_COLON + ItemLogColumn.priority + RIGHT_PARANTHESIS;

        public static final String insertOrderLog = INSERT_INTO + OrderLogColumn.tableName + LEFT_PARANTHESIS +
                OrderLogColumn.id + COMMA +
                OrderLogColumn.errorCode + COMMA +
                OrderLogColumn.mobile + COMMA +
                OrderLogColumn.message + COMMA +
                OrderLogColumn.updatedValue + COMMA +
                OrderLogColumn.priority + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + OrderLogColumn.id +
                COMMA_COLON + OrderLogColumn.errorCode +
                COMMA_COLON + OrderLogColumn.mobile +
                COMMA_COLON + OrderLogColumn.message +
                COMMA_COLON + OrderLogColumn.updatedValue +
                COMMA_COLON + OrderLogColumn.priority + RIGHT_PARANTHESIS;
    }

    public static final class PlaceQuery {
        public static final String notDeleted = PlaceColumn.isDelete + " = 0";

        public static final String insertPlace = INSERT_INTO + PlaceColumn.tableName + LEFT_PARANTHESIS +
                PlaceColumn.name + COMMA +
                PlaceColumn.iconUrl + COMMA +
                PlaceColumn.address + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + PlaceColumn.name +
                COMMA_COLON + PlaceColumn.iconUrl +
                COMMA_COLON + PlaceColumn.address + RIGHT_PARANTHESIS;

        public static final String getAllPlaces = SELECT +
                PlaceColumn.id + COMMA +
                PlaceColumn.name + COMMA +
                PlaceColumn.iconUrl + COMMA +
                PlaceColumn.address + FROM + PlaceColumn.tableName + WHERE +
                notDeleted +
                ORDER_BY + PlaceColumn.name + ASC;

        public static final String getPlaceById = SELECT +
                PlaceColumn.id + COMMA +
                PlaceColumn.name + COMMA +
                PlaceColumn.iconUrl + COMMA +
                PlaceColumn.address + FROM + PlaceColumn.tableName + WHERE +
                PlaceColumn.id + EQUAL_COLON + PlaceColumn.id;

        public static final String updatePlace = UPDATE + PlaceColumn.tableName + SET +
                PlaceColumn.name + EQUAL_COLON + PlaceColumn.name + COMMA +
                PlaceColumn.iconUrl + EQUAL_COLON + PlaceColumn.iconUrl + COMMA +
                PlaceColumn.address + EQUAL_COLON + PlaceColumn.address + WHERE +
                PlaceColumn.id + EQUAL_COLON + PlaceColumn.id;

        public static final String deletePlace = UPDATE + PlaceColumn.tableName + SET +
                PlaceColumn.isDelete + " = 1" + WHERE +
                PlaceColumn.id + EQUAL_COLON + PlaceColumn.id;

        public static final String unDeletePlace = UPDATE + PlaceColumn.tableName + SET +
                PlaceColumn.isDelete + " = 0" + WHERE +
                PlaceColumn.id + EQUAL_COLON + PlaceColumn.id;
    }

    public static final class ConfigurationQuery {
        public static final String insertConfiguration = INSERT_INTO + ConfigurationColumn.tableName + LEFT_PARANTHESIS +
                ConfigurationColumn.shopId + COMMA +
                ConfigurationColumn.merchantId + COMMA +
                ConfigurationColumn.deliveryPrice + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + ConfigurationColumn.shopId +
                COMMA_COLON + ConfigurationColumn.merchantId +
                COMMA_COLON + ConfigurationColumn.deliveryPrice + RIGHT_PARANTHESIS;

        public static final String getConfigurationByShopId = SELECT +
                ConfigurationColumn.shopId + COMMA +
                ConfigurationColumn.merchantId + COMMA +
                ConfigurationColumn.deliveryPrice + COMMA +
                ConfigurationColumn.isDeliveryAvailable + COMMA +
                ConfigurationColumn.isOrderTaken + FROM + ConfigurationColumn.tableName + WHERE +
                ConfigurationColumn.shopId + EQUAL_COLON + ConfigurationColumn.shopId;

        public static final String updateConfiguration = UPDATE + ConfigurationColumn.tableName + SET +
                ConfigurationColumn.merchantId + EQUAL_COLON + ConfigurationColumn.merchantId + COMMA +
                ConfigurationColumn.deliveryPrice + EQUAL_COLON + ConfigurationColumn.deliveryPrice + COMMA +
                ConfigurationColumn.isDeliveryAvailable + EQUAL_COLON + ConfigurationColumn.isDeliveryAvailable + COMMA +
                ConfigurationColumn.isOrderTaken + EQUAL_COLON + ConfigurationColumn.isOrderTaken + WHERE +
                ConfigurationColumn.shopId + EQUAL_COLON + ConfigurationColumn.shopId;
    }

    public static final class ItemQuery {
        public static final String notDeleted = ItemColumn.isDelete + " = 0";

        public static final String insertItem = INSERT_INTO + ItemColumn.tableName + LEFT_PARANTHESIS +
                ItemColumn.name + COMMA +
                ItemColumn.price + COMMA +
                ItemColumn.photoUrl + COMMA +
                ItemColumn.category + COMMA +
                ItemColumn.shopId + COMMA +
                ItemColumn.isVeg + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + ItemColumn.name +
                COMMA_COLON + ItemColumn.price +
                COMMA_COLON + ItemColumn.photoUrl +
                COMMA_COLON + ItemColumn.category +
                COMMA_COLON + ItemColumn.shopId +
                COMMA_COLON + ItemColumn.isVeg + RIGHT_PARANTHESIS;

        public static final String getItemById = SELECT +
                ItemColumn.id + COMMA +
                ItemColumn.name + COMMA +
                ItemColumn.price + COMMA +
                ItemColumn.photoUrl + COMMA +
                ItemColumn.category + COMMA +
                ItemColumn.shopId + COMMA +
                ItemColumn.isVeg + COMMA +
                ItemColumn.isAvailable + FROM + ItemColumn.tableName + WHERE +
                ItemColumn.id + EQUAL_COLON + ItemColumn.id;

        public static final String getItemsByShopId = SELECT +
                ItemColumn.id + COMMA +
                ItemColumn.name + COMMA +
                ItemColumn.price + COMMA +
                ItemColumn.photoUrl + COMMA +
                ItemColumn.category + COMMA +
                ItemColumn.shopId + COMMA +
                ItemColumn.isVeg + COMMA +
                ItemColumn.isAvailable + FROM + ItemColumn.tableName + WHERE +
                ItemColumn.shopId + EQUAL_COLON + ItemColumn.shopId + AND +
                notDeleted;

        public static final String getItemsByName = SELECT +
                ItemColumn.id + COMMA +
                ItemColumn.name + COMMA +
                ItemColumn.price + COMMA +
                ItemColumn.photoUrl + COMMA +
                ItemColumn.category + COMMA +
                ItemColumn.shopId + COMMA +
                ItemColumn.isVeg + COMMA +
                ItemColumn.isAvailable + FROM + ItemColumn.tableName + WHERE +
                ItemColumn.name + LIKE + COLON + ItemColumn.name + AND +
                notDeleted + AND +
                ItemColumn.shopId + IN + LEFT_PARANTHESIS + ShopQuery.getShopIdByPlaceId + RIGHT_PARANTHESIS;

        public static final String updateItem = UPDATE + ItemColumn.tableName + SET +
                ItemColumn.name + EQUAL_COLON + ItemColumn.name + COMMA +
                ItemColumn.price + EQUAL_COLON + ItemColumn.price + COMMA +
                ItemColumn.photoUrl + EQUAL_COLON + ItemColumn.photoUrl + COMMA +
                ItemColumn.category + EQUAL_COLON + ItemColumn.category + COMMA +
                ItemColumn.isVeg + EQUAL_COLON + ItemColumn.isVeg + COMMA +
                ItemColumn.isAvailable + EQUAL_COLON + ItemColumn.isAvailable + WHERE +
                ItemColumn.id + EQUAL_COLON + ItemColumn.id;

        public static final String deleteItem = UPDATE + ItemColumn.tableName + SET +
                ItemColumn.isDelete + " = 1" + WHERE +
                ItemColumn.id + EQUAL_COLON + ItemColumn.id;

        public static final String unDeleteItem = UPDATE + ItemColumn.tableName + SET +
                ItemColumn.isDelete + " = 0" + WHERE +
                ItemColumn.id + EQUAL_COLON + ItemColumn.id;
    }

    public static final class OrderItemQuery {
        public static final String insertOrderItem = INSERT_INTO + OrderItemColumn.tableName + LEFT_PARANTHESIS +
                OrderItemColumn.orderId + COMMA +
                OrderItemColumn.itemId + COMMA +
                OrderItemColumn.quantity + COMMA +
                OrderItemColumn.price + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + OrderItemColumn.orderId +
                COMMA_COLON + OrderItemColumn.itemId +
                COMMA_COLON + OrderItemColumn.quantity +
                COMMA_COLON + OrderItemColumn.price + RIGHT_PARANTHESIS;

        public static final String getItemByOrderId = SELECT +
                OrderItemColumn.orderId + COMMA +
                OrderItemColumn.itemId + COMMA +
                OrderItemColumn.quantity + COMMA +
                OrderItemColumn.price + FROM + OrderItemColumn.tableName + WHERE +
                OrderItemColumn.orderId + EQUAL_COLON + OrderItemColumn.orderId;
    }

    public static final class OrderQuery {
        public static final String orderByDesc = ORDER_BY + OrderColumn.date + DESC;
        public static final String pageNum = "pageNum";
        public static final String pageCount = "pageCount";

        public static final String insertOrder = INSERT_INTO + OrderColumn.tableName + LEFT_PARANTHESIS +
                OrderColumn.id + COMMA +
                OrderColumn.mobile + COMMA +
                OrderColumn.shopId + COMMA +
                OrderColumn.price + COMMA +
                OrderColumn.deliveryPrice + COMMA +
                OrderColumn.deliveryLocation + COMMA +
                OrderColumn.cookingInfo + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + OrderColumn.id +
                COMMA_COLON + OrderColumn.mobile +
                COMMA_COLON + OrderColumn.shopId +
                COMMA_COLON + OrderColumn.price +
                COMMA_COLON + OrderColumn.deliveryPrice +
                COMMA_COLON + OrderColumn.deliveryLocation +
                COMMA_COLON + OrderColumn.cookingInfo + RIGHT_PARANTHESIS;

        public static final String getOrderByOrderId = SELECT +
                OrderColumn.id + COMMA +
                OrderColumn.mobile + COMMA +
                OrderColumn.shopId + COMMA +
                OrderColumn.date + COMMA +
                OrderColumn.status + COMMA +
                OrderColumn.lastStatusUpdatedTime + COMMA +
                OrderColumn.price + COMMA +
                OrderColumn.deliveryPrice + COMMA +
                OrderColumn.deliveryLocation + COMMA +
                OrderColumn.cookingInfo + COMMA +
                OrderColumn.rating + COMMA +
                OrderColumn.secretKey + FROM + OrderColumn.tableName + WHERE +
                OrderColumn.id + EQUAL_COLON + OrderColumn.id;

        public static final String getOrderByMobile = SELECT +
                OrderColumn.id +
                FROM + OrderColumn.tableName + WHERE +
                OrderColumn.mobile + EQUAL_COLON + OrderColumn.mobile +
                orderByDesc;

        public static final String getOrderByShopIdPagination = SELECT +
                OrderColumn.id +
                FROM + OrderColumn.tableName + WHERE +
                OrderColumn.shopId + EQUAL_COLON + OrderColumn.shopId + AND + LEFT_PARANTHESIS +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.CANCELLED_BY_SELLER.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.CANCELLED_BY_USER.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.COMPLETED.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.REFUND_INITIATED.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.REFUND_COMPLETED.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.DELIVERED.name() + SINGLE_QUOTE + RIGHT_PARANTHESIS +
                orderByDesc;

        public static final String getOrderByShopId = SELECT +
                OrderColumn.id +
                FROM + OrderColumn.tableName + WHERE +
                OrderColumn.shopId + EQUAL_COLON + OrderColumn.shopId + AND + LEFT_PARANTHESIS +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.PLACED.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.ACCEPTED.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.READY.name() + SINGLE_QUOTE + CONCATENATION_OPERATOR +
                OrderColumn.status + EQUALS + SINGLE_QUOTE + OrderStatus.OUT_FOR_DELIVERY.name() + SINGLE_QUOTE + RIGHT_PARANTHESIS +
                orderByDesc;

        public static final String getOrderByStatus = SELECT +
                OrderColumn.id + COMMA +
                OrderColumn.mobile + COMMA +
                OrderColumn.shopId + COMMA +
                OrderColumn.date + COMMA +
                OrderColumn.status + COMMA +
                OrderColumn.lastStatusUpdatedTime + COMMA +
                OrderColumn.price + COMMA +
                OrderColumn.deliveryPrice + COMMA +
                OrderColumn.deliveryLocation + COMMA +
                OrderColumn.cookingInfo + COMMA +
                OrderColumn.rating + COMMA +
                OrderColumn.secretKey + FROM + OrderColumn.tableName + WHERE +
                OrderColumn.status + EQUAL_COLON + OrderColumn.status;

        public static final String updateOrderRating = UPDATE + OrderColumn.tableName + SET +
                OrderColumn.rating + EQUAL_COLON + OrderColumn.rating + WHERE +
                OrderColumn.id + EQUAL_COLON + OrderColumn.id;

        public static final String updateOrderKey = UPDATE + OrderColumn.tableName + SET +
                OrderColumn.secretKey + EQUAL_COLON + OrderColumn.secretKey + WHERE +
                OrderColumn.id + EQUAL_COLON + OrderColumn.id;

        public static final String updateOrderStatus = UPDATE + OrderColumn.tableName + SET +
                OrderColumn.status + EQUAL_COLON + OrderColumn.status + COMMA +
                OrderColumn.lastStatusUpdatedTime + EQUALS + CURRENT_TIMESTAMP + WHERE +
                OrderColumn.id + EQUAL_COLON + OrderColumn.id;
    }

    public static final class RatingQuery {
        public static final String getRatingByShopId = SELECT +
                RatingColumn.shopId + COMMA +
                RatingColumn.rating + COMMA +
                RatingColumn.userCount + FROM + RatingColumn.tableName + WHERE +
                RatingColumn.shopId + EQUAL_COLON + RatingColumn.shopId;

        public static final String updateRating = UPDATE + RatingColumn.tableName + SET +
                RatingColumn.rating + EQUAL_COLON + RatingColumn.rating + COMMA +
                RatingColumn.userCount + EQUAL_COLON + RatingColumn.userCount + WHERE +
                RatingColumn.shopId + EQUAL_COLON + RatingColumn.shopId;
    }

    public static final class UserInviteQuery {
        public static final String notDeleted = UserInviteColumn.isDelete + " = 0";

        public static final String inviteSeller = INSERT_INTO + UserInviteColumn.tableName + LEFT_PARANTHESIS +
                UserInviteColumn.mobile + COMMA +
                UserInviteColumn.role + COMMA +
                UserInviteColumn.shopId + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + UserInviteColumn.mobile +
                COMMA_COLON + UserInviteColumn.role +
                COMMA_COLON + UserInviteColumn.shopId + RIGHT_PARANTHESIS;

        public static final String verifyInvite = SELECT +
                UserInviteColumn.mobile + COMMA +
                UserInviteColumn.role + COMMA +
                UserInviteColumn.shopId + COMMA +
                UserInviteColumn.invitedAt + FROM + UserInviteColumn.tableName + WHERE +
                UserInviteColumn.mobile + EQUAL_COLON + UserInviteColumn.mobile + AND +
                UserInviteColumn.shopId + EQUAL_COLON + UserInviteColumn.shopId + AND +
                notDeleted + AND +
                TIMESTAMPDIFF + LEFT_PARANTHESIS + MINUTE + COMMA + UserInviteColumn.invitedAt + COMMA + CURRENT_TIMESTAMP + RIGHT_PARANTHESIS + LESS_THAN + 15 +
                ORDER_BY + UserInviteColumn.invitedAt + DESC + LIMIT + 1;

        public static final String deleteInvite = UPDATE + UserInviteColumn.tableName + SET +
                UserInviteColumn.isDelete + " = 1" + WHERE +
                UserInviteColumn.mobile + EQUAL_COLON + UserInviteColumn.mobile + AND +
                UserInviteColumn.role + EQUAL_COLON + UserInviteColumn.role + AND +
                UserInviteColumn.shopId + EQUAL_COLON + UserInviteColumn.shopId;
    }

    public static final class ShopQuery {
        public static final String notDeleted = ShopColumn.isDelete + " = 0";

        public static final String insertShop = INSERT_INTO + ShopColumn.tableName + LEFT_PARANTHESIS +
                ShopColumn.name + COMMA +
                ShopColumn.photoUrl + COMMA +
                ShopColumn.mobile + COMMA +
                ShopColumn.placeId + COMMA +
                ShopColumn.coverUrls + COMMA +
                ShopColumn.openingTime + COMMA +
                ShopColumn.closingTime + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + ShopColumn.name +
                COMMA_COLON + ShopColumn.photoUrl +
                COMMA_COLON + ShopColumn.mobile +
                COMMA_COLON + ShopColumn.placeId +
                COMMA_COLON + ShopColumn.coverUrls +
                COMMA_COLON + ShopColumn.openingTime +
                COMMA_COLON + ShopColumn.closingTime + RIGHT_PARANTHESIS;

        public static final String getShopByPlaceId = SELECT +
                ShopColumn.id + COMMA +
                ShopColumn.name + COMMA +
                ShopColumn.photoUrl + COMMA +
                ShopColumn.coverUrls + COMMA +
                ShopColumn.mobile + COMMA +
                ShopColumn.placeId + COMMA +
                ShopColumn.openingTime + COMMA +
                ShopColumn.closingTime + FROM + ShopColumn.tableName + WHERE +
                ShopColumn.placeId + EQUAL_COLON + ShopColumn.placeId + AND + notDeleted;

        public static final String getShopIdByPlaceId = SELECT +
                ShopColumn.id + FROM + ShopColumn.tableName + WHERE +
                ShopColumn.placeId + EQUAL_COLON + ShopColumn.placeId + AND + notDeleted;

        public static final String getShopById = SELECT +
                ShopColumn.id + COMMA +
                ShopColumn.name + COMMA +
                ShopColumn.photoUrl + COMMA +
                ShopColumn.coverUrls + COMMA +
                ShopColumn.mobile + COMMA +
                ShopColumn.placeId + COMMA +
                ShopColumn.openingTime + COMMA +
                ShopColumn.closingTime + FROM + ShopColumn.tableName + WHERE +
                ShopColumn.id + EQUAL_COLON + ShopColumn.id;

        public static final String updateShop = UPDATE + ShopColumn.tableName + SET +
                ShopColumn.name + EQUAL_COLON + ShopColumn.name + COMMA +
                ShopColumn.photoUrl + EQUAL_COLON + ShopColumn.photoUrl + COMMA +
                ShopColumn.coverUrls + EQUAL_COLON + ShopColumn.coverUrls + COMMA +
                ShopColumn.mobile + EQUAL_COLON + ShopColumn.mobile + COMMA +
                ShopColumn.openingTime + EQUAL_COLON + ShopColumn.openingTime + COMMA +
                ShopColumn.closingTime + EQUAL_COLON + ShopColumn.closingTime + WHERE +
                ShopColumn.id + EQUAL_COLON + ShopColumn.id;

        public static final String deleteShop = UPDATE + ShopColumn.tableName + SET +
                ShopColumn.isDelete + " = 1" + WHERE +
                ShopColumn.id + EQUAL_COLON + ShopColumn.id;

        public static final String unDeleteShop = UPDATE + ShopColumn.tableName + SET +
                ShopColumn.isDelete + " = 0" + WHERE +
                ShopColumn.id + EQUAL_COLON + ShopColumn.id;
    }

    public static final class TransactionQuery {

        public static final String pageNum = "pageNum";
        public static final String pageCount = "pageCount";

        public static final String insertTransaction = INSERT_INTO + TransactionColumn.tableName + LEFT_PARANTHESIS +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + TransactionColumn.transactionId +
                COMMA_COLON + TransactionColumn.bankTransactionId +
                COMMA_COLON + TransactionColumn.currency +
                COMMA_COLON + TransactionColumn.responseCode +
                COMMA_COLON + TransactionColumn.responseMessage +
                COMMA_COLON + TransactionColumn.gatewayName +
                COMMA_COLON + TransactionColumn.bankName +
                COMMA_COLON + TransactionColumn.paymentMode +
                COMMA_COLON + TransactionColumn.checksumHash + RIGHT_PARANTHESIS;

        public static final String getTransaction = SELECT +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + FROM + TransactionColumn.tableName + WHERE +
                TransactionColumn.transactionId + EQUAL_COLON + TransactionColumn.transactionId;

        public static final String getTransactionByOrderId = SELECT +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + FROM + TransactionColumn.tableName + WHERE +
                TransactionColumn.orderId + EQUAL_COLON + TransactionColumn.orderId;

        public static final String getTransactionByMobile = SELECT +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + FROM + TransactionColumn.tableName + WHERE +
                TransactionColumn.orderId + IN +
                LEFT_PARANTHESIS + OrderQuery.getOrderByMobile + RIGHT_PARANTHESIS +
                LIMIT + COLON + pageCount + OFFSET + COLON + pageNum;

        public static final String getTransactionByShopIdPagination = SELECT +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + FROM + TransactionColumn.tableName + WHERE +
                TransactionColumn.orderId + IN +
                LEFT_PARANTHESIS + OrderQuery.getOrderByShopIdPagination + RIGHT_PARANTHESIS +
                LIMIT + COLON + pageCount + OFFSET + COLON + pageNum;

        public static final String getTransactionByShopId = SELECT +
                TransactionColumn.transactionId + COMMA +
                TransactionColumn.orderId + COMMA +
                TransactionColumn.bankTransactionId + COMMA +
                TransactionColumn.currency + COMMA +
                TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + COMMA +
                TransactionColumn.gatewayName + COMMA +
                TransactionColumn.bankName + COMMA +
                TransactionColumn.paymentMode + COMMA +
                TransactionColumn.checksumHash + FROM + TransactionColumn.tableName + WHERE +
                TransactionColumn.orderId + IN +
                LEFT_PARANTHESIS + OrderQuery.getOrderByShopId + RIGHT_PARANTHESIS;


        public static final String updateTransaction = UPDATE + TransactionColumn.tableName + SET +
                TransactionColumn.responseCode + EQUAL_COLON + TransactionColumn.responseCode + COMMA +
                TransactionColumn.responseMessage + EQUAL_COLON + TransactionColumn.responseMessage + COMMA +
                TransactionColumn.date + EQUALS + CURRENT_TIMESTAMP + WHERE +
                TransactionColumn.orderId + EQUAL_COLON + TransactionColumn.orderId;
    }

    public static final class UserPlaceQuery {
        public static final String insertUserPlace = INSERT_INTO + UserPlaceColumn.tableName + LEFT_PARANTHESIS +
                UserPlaceColumn.mobile + COMMA +
                UserPlaceColumn.placeId + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + UserPlaceColumn.mobile +
                COMMA_COLON + UserPlaceColumn.placeId + RIGHT_PARANTHESIS;

        public static final String getPlaceByMobile = SELECT +
                UserPlaceColumn.mobile + COMMA +
                UserPlaceColumn.placeId + FROM + UserPlaceColumn.tableName + WHERE +
                UserPlaceColumn.mobile + EQUAL_COLON + UserPlaceColumn.mobile;

        public static final String updatePlaceByMobile = UPDATE + UserPlaceColumn.tableName + SET +
                UserPlaceColumn.placeId + EQUAL_COLON + UserPlaceColumn.placeId + WHERE +
                UserPlaceColumn.mobile + EQUAL_COLON + UserPlaceColumn.mobile;
    }

    public static final class UserQuery {
        public static final String notDeleted = UserColumn.isDelete + " = 0";

        public static final String insertUser = INSERT_INTO + UserColumn.tableName + LEFT_PARANTHESIS +
                UserColumn.oauthId + COMMA +
                UserColumn.mobile + COMMA +
                UserColumn.role + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + UserColumn.oauthId +
                COMMA_COLON + UserColumn.mobile +
                COMMA_COLON + UserColumn.role + RIGHT_PARANTHESIS;

        public static final String getUserByMobile = SELECT +
                UserColumn.oauthId + COMMA +
                UserColumn.name + COMMA +
                UserColumn.email + COMMA +
                UserColumn.mobile + COMMA +
                UserColumn.role + FROM + UserColumn.tableName + WHERE +
                UserColumn.mobile + EQUAL_COLON + UserColumn.mobile;

        public static final String loginUserByMobileOauth = getUserByMobile + AND +
                UserColumn.oauthId + EQUAL_COLON + UserColumn.oauthId + AND +
                notDeleted;

        public static final String validateUser = loginUserByMobileOauth + AND +
                UserColumn.role + EQUAL_COLON + UserColumn.role;

        public static final String getSellerByShopId = SELECT +
                UserColumn.oauthId + COMMA +
                UserColumn.name + COMMA +
                UserColumn.email + COMMA +
                UserColumn.mobile + COMMA +
                UserColumn.role + FROM + UserColumn.tableName + WHERE +
                notDeleted + AND +
                UserColumn.role + EQUALS + SINGLE_QUOTE + SELLER.name() + SINGLE_QUOTE + AND +
                UserColumn.mobile + IN + LEFT_PARANTHESIS + SELECT +
                UserShopColumn.mobile + FROM + UserShopColumn.tableName + WHERE +
                UserShopColumn.shopId + EQUAL_COLON + UserShopColumn.shopId + RIGHT_PARANTHESIS;

        public static final String updateUser = UPDATE + UserColumn.tableName + SET +
                UserColumn.name + EQUAL_COLON + UserColumn.name + COMMA +
                UserColumn.email + EQUAL_COLON + UserColumn.email + WHERE +
                UserColumn.mobile + EQUAL_COLON + UserColumn.mobile;

        public static final String updateRole = UPDATE + UserColumn.tableName + SET +
                UserColumn.role + EQUAL_COLON + UserColumn.role + WHERE +
                UserColumn.mobile + EQUAL_COLON + UserColumn.mobile;
    }

    public static final class UserShopQuery {
        public static final String insertUserShop = INSERT_INTO + UserShopColumn.tableName + LEFT_PARANTHESIS +
                UserShopColumn.mobile + COMMA +
                UserShopColumn.shopId + RIGHT_PARANTHESIS + VALUES + LEFT_PARANTHESIS +
                COLON + UserShopColumn.mobile +
                COMMA_COLON + UserShopColumn.shopId + RIGHT_PARANTHESIS;

        public static final String getUserByShopId = SELECT +
                UserShopColumn.mobile + COMMA +
                UserShopColumn.shopId + FROM + UserShopColumn.tableName + WHERE +
                UserShopColumn.shopId + EQUAL_COLON + UserShopColumn.shopId;

        public static final String getShopByMobile = SELECT +
                UserShopColumn.mobile + COMMA +
                UserShopColumn.shopId + FROM + UserShopColumn.tableName + WHERE +
                UserShopColumn.mobile + EQUAL_COLON + UserShopColumn.mobile;

        public static final String updateShopByMobile = UPDATE + UserShopColumn.tableName + SET +
                UserShopColumn.shopId + EQUAL_COLON + UserShopColumn.shopId + WHERE +
                UserShopColumn.mobile + EQUAL_COLON + UserShopColumn.mobile;

        public static final String deleteUser = DELETE_FROM + UserShopColumn.tableName + WHERE +
                UserShopColumn.mobile + EQUAL_COLON + UserShopColumn.mobile + AND +
                UserShopColumn.shopId + EQUAL_COLON + UserShopColumn.shopId;
    }
}
