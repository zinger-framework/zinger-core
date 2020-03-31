package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.OrderLogColumn.*;

public class OrderLogRowMapperLambda {

    public static final RowMapper<OrderLogModel> ordersLogRowMapperLambda = (rs, rownum) -> {
        OrderLogModel orders = new OrderLogModel();
        orders.setId(rs.getString(id));
        orders.setErrorCode(rs.getInt(errorCode));
        orders.setMobile(rs.getString(mobile));
        orders.setMessage(rs.getString(message));
        orders.setUpdatedValue(rs.getString(updatedValue));
        orders.setDate(rs.getTimestamp(date));
        orders.setPriority(Priority.valueOf(rs.getString(priority)));
        return orders;
    };
}
