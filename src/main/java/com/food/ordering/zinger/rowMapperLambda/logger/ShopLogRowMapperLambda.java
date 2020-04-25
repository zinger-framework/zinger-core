package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.ShopLogColumn.*;

public class ShopLogRowMapperLambda {
    public static final RowMapper<ShopLogModel> shopLogRowMapperLambda = (rs, rownum) -> {
        ShopLogModel shop = new ShopLogModel();
        shop.setId(rs.getInt(id));
        shop.setErrorCode(rs.getInt(errorCode));
        shop.setMessage(rs.getString(message));
        shop.setUpdatedValue(rs.getString(updatedValue));
        shop.setDate(rs.getTimestamp(date));
        shop.setPriority(Priority.valueOf(rs.getString(priority)));
        return shop;
    };
}
