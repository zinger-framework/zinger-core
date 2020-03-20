package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.UserShopLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.UserShopLogColumn.*;

public class UserShopLogRowMapperLambda {
	 public static final RowMapper<UserShopLogModel> usersShopLogRowMapperLambda = (rs, rownum) -> {
	        UserShopLogModel usersShop = new UserShopLogModel();
	        usersShop.setUsersMobile(rs.getString(usersMobile));
	        usersShop.setErrorCode(rs.getInt(errorCode));
	        usersShop.setMobile(rs.getString(mobile));
	        usersShop.setMessage(rs.getString(message));
	        usersShop.setUpdatedValue(rs.getString(updatedValue));
	        usersShop.setDate(rs.getTimestamp(date));
	        usersShop.setPriority(Priority.valueOf(rs.getString(priority)));
	        return usersShop;
	    };

}
