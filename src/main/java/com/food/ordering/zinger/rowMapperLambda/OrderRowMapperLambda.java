package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.constant.Enums.OrderStatus;
import com.food.ordering.zinger.model.OrderModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.OrderColumn.*;

public class OrderRowMapperLambda {
    public static final RowMapper<OrderModel> orderRowMapperLambda = (rs, rownum) -> {
        OrderModel orderModel = new OrderModel();
        orderModel.setId(rs.getString(id));

        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        orderModel.setUserModel(userModel);


        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        orderModel.setShopModel(shopModel);

        orderModel.setDate(rs.getTimestamp(date));

        if (rs.getString(status) != null)
            orderModel.setOrderStatus(OrderStatus.valueOf(rs.getString(status)));

        orderModel.setLastStatusUpdatedTime(rs.getTimestamp(lastStatusUpdatedTime));
        orderModel.setPrice(rs.getDouble(price));
        orderModel.setDeliveryPrice(rs.getDouble(deliveryPrice));
        orderModel.setDeliveryLocation(rs.getString(deliveryLocation));
        orderModel.setCookingInfo(rs.getString(cookingInfo));
        orderModel.setRating(rs.getDouble(rating));
        orderModel.setSecretKey(rs.getString(secretKey));
        return orderModel;
    };
}
