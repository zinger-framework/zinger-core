package com.food.ordering.zinger.rowMapperLambda;


import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.UsersShopLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.UsersShopLogColumn.*;

public class UsersShopLogRowMapperLambda {
	 public static final RowMapper<UsersShopLogModel> usersShopLogRowMapperLambda = (rs, rownum) -> {
	        UsersShopLogModel usersShop = new UsersShopLogModel();
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