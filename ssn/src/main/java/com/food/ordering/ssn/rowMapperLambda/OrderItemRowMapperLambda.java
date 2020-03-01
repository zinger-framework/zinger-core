package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.*;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.OrderItemColumn.*;

public class OrderItemRowMapperLambda {
	public static final RowMapper<OrderItemModel> orderItemRowMapperLambda = (rs, rownum) -> {
		OrderItemModel orderItemModel = new OrderItemModel();

		OrderModel orderModel = new OrderModel();
		orderModel.setId(rs.getInt(orderId));
		orderItemModel.setOrderModel(orderModel);

		ItemModel itemModel = new ItemModel();
		itemModel.setId(rs.getInt(itemId));
		orderItemModel.setItemModel(itemModel);

		orderItemModel.setQuantity(rs.getInt(quantity));
		orderItemModel.setPrice(rs.getDouble(price));

		return orderItemModel;
	};
}
