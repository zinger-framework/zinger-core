package com.food.ordering.zinger.constant;

public class Constant {
    public static final String authIdSA = "sa_auth";
    public static final String idSA = "sa_id";
    public static final String roleSA = "sa_role";

    public static final String deliveryOrderFlag = "D";
    public static final String pickUpOrderFlag = "P";


    public static final class VerifyPricingProcedure {

        public static final String procedureName =  "verify_pricing";

        // I/P parameters
        public static final String itemList =  "item_list";
        public static final String shopId =  "s_id";
        public static final String orderType = "order_type";

        // O/P parameters
        public static final String totalPrice = "total_price";
        public static final String merchantId = "m_id";

    }

}
