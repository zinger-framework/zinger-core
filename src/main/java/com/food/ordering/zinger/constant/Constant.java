package com.food.ordering.zinger.constant;

public class Constant {
    public static final String deliveryOrderFlag = "D";
    public static final String pickUpOrderFlag = "P";

    public static final String transactionFlag = "T";
    public static final String refundFlag = "R";

    public static final String notificationTitle = "title";
    public static final String notificationMessage = "message";
    public static final String notificationType = "type";
    public static final String notificationPayload = "payload";


    public static final class VerifyPricingProcedure {
        public static final String procedureName = "verify_pricing";

        // I/P parameters
        public static final String itemList = "item_list";
        public static final String shopId = "s_id";
        public static final String orderType = "order_type";

        // O/P parameters
        public static final String totalPrice = "total_price";
        public static final String merchantId = "m_id";
    }

    public static final class OrderStatusUpdate {
        public static final String procedureName = "order_status_update";

        // I/P parameters
        public static final String orderId = "o_id";
        public static final String newStatus = "new_status";
        public static final String newSecretKey = "new_secret_key";

        // O/P parameters
        public static final String result = "result";
    }
}
