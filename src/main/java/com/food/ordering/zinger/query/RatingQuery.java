package com.food.ordering.zinger.query;

import static com.food.ordering.zinger.column.RatingColumn.*;

public class RatingQuery {
    public static final String getRatingByShopId = "SELECT " + shopId + ", " + rating + ", " + userCount + " FROM " + tableName + " WHERE " + shopId + " = :" + shopId;

    public static final String updateRating = "UPDATE " + tableName + " SET " + rating + " = :" + rating + ", " + userCount + " = :" + userCount + " WHERE " + shopId + " = :" + shopId;
}
