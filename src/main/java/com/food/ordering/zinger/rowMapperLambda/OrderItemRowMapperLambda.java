package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.OrderItemModel;
import com.food.ordering.zinger.model.OrderModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.OrderItemColumn.*;

public class OrderItemRowMapperLambda {
    public static final RowMapper<OrderItemModel> orderItemRowMapperLambda = (rs, rownum) -> {
        OrderItemModel orderItemModel = new OrderItemModel();

        OrderModel orderModel = new OrderModel();
        orderModel.setId(rs.getString(orderId));
        orderItemModel.setOrderModel(orderModel);

        ItemModel itemModel = new ItemModel();
        itemModel.setId(rs.getInt(itemId));
        orderItemModel.setItemModel(itemModel);

        orderItemModel.setQuantity(rs.getInt(quantity));
        orderItemModel.setPrice(rs.getDouble(price));

        return orderItemModel;
    };
}
