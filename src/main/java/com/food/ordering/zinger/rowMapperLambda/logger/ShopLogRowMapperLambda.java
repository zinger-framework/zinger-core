package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.ShopLogColumn.*;

public class ShopLogRowMapperLambda {
	public static final RowMapper<ShopLogModel> shopLogRowMapperLambda = (rs, rownum) -> {
	        ShopLogModel shop = new ShopLogModel();
	        shop.setId(rs.getInt(id));
	        shop.setErrorCode(rs.getInt(errorCode));
	        shop.setMobile(rs.getString(mobile));
	        shop.setMessage(rs.getString(message));
	        shop.setUpdatedValue(rs.getString(updatedValue));
	        shop.setDate(rs.getTimestamp(date));
	        shop.setPriority(Priority.valueOf(rs.getString(priority)));
	        return shop;
	    };
}
