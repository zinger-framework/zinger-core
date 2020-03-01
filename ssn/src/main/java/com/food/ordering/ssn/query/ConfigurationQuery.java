package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.ConfigurationColumn.*;

public class ConfigurationQuery {
    public static final String insertConfiguration = "INSERT INTO " + tableName + "(" + shopId + ", " + deliveryPrice + ") VALUES(:" + shopId + ", :" + deliveryPrice + ")";

    public static final String getConfigurationByShopId = "SELECT " + shopId + ", " + deliveryPrice + ", " + isDeliveryAvailable + ", " + isOrderTaken + " FROM " + tableName + " WHERE " + shopId + " = :" + shopId;

    public static final String updateConfiguration = "UPDATE " + tableName + " SET " + deliveryPrice + " = :" + deliveryPrice + ", " + isDeliveryAvailable + " = :" + isDeliveryAvailable + ", " + isOrderTaken + " = :" + isOrderTaken + " WHERE " + shopId + " = :" + shopId;
}
