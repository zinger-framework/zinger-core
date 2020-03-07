package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.enums.OrderStatus;
import com.food.ordering.ssn.model.OrderModel;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.model.TransactionModel;
import com.food.ordering.ssn.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.OrderColumn.*;

public class OrderRowMapperLambda {
    public static final RowMapper<OrderModel> orderRowMapperLambda = (rs, rownum) -> {
        OrderModel orderModel = new OrderModel();
        orderModel.setId(rs.getInt(id));

        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        orderModel.setUserModel(userModel);

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setTransactionId(rs.getString(transactionId));
        orderModel.setTransactionModel(transactionModel);

        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        orderModel.setShopModel(shopModel);

        orderModel.setDate(rs.getDate(date));
        orderModel.setOrderStatus(OrderStatus.valueOf(rs.getString(status)));
        orderModel.setLastStatusUpdatedTime(rs.getDate(lastStatusUpdatedTime));
        orderModel.setPrice(rs.getDouble(price));
        orderModel.setDeliveryPrice(rs.getDouble(deliveryPrice));
        orderModel.setDeliveryLocation(rs.getString(deliveryLocation));
        orderModel.setCookingInfo(rs.getString(cookingInfo));
        orderModel.setRating(rs.getDouble(rating));
        orderModel.setSecretKey(rs.getString(secretKey));
        return orderModel;
    };
}
