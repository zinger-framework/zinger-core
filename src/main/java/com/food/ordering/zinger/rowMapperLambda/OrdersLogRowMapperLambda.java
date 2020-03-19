package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.OrdersLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.OrdersLogColumn.*;

public class OrdersLogRowMapperLambda {

	 public static final RowMapper<OrdersLogModel> ordersLogRowMapperLambda = (rs, rownum) -> {
	        OrdersLogModel orders = new OrdersLogModel();
	        orders.setId(rs.getInt(id));
	        orders.setErrorCode(rs.getInt(errorCode));
	        orders.setMobile(rs.getString(mobile));
	        orders.setMessage(rs.getString(message));
	        orders.setUpdatedValue(rs.getString(updatedValue));
	        orders.setDate(rs.getTimestamp(date));
	        orders.setPriority(Priority.valueOf(rs.getString(priority)));
	        return orders;
	    };

}