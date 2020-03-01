package com.food.ordering.ssn.query;

import static com.food.ordering.ssn.column.RatingColumn.*;

public class RatingQuery {
    public static final String insertRating = "INSERT INTO " + tableName + "(" + shopId + ") VALUES(:" + shopId + ")";

    public static final String getRatingByShopId = "SELECT " + shopId + ", " + rating + ", " + userCount + " FROM " + tableName + " WHERE " + shopId + " = :" + shopId;
}
