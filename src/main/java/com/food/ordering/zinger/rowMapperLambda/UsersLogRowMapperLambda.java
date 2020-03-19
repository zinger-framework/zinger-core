package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.UsersLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.UsersLogColumn.*;

public class UsersLogRowMapperLambda {
	public static final RowMapper<UsersLogModel> usersLogRowMapperLambda = (rs, rownum) -> {
        UsersLogModel users = new UsersLogModel();
        users.setUsersMobile(rs.getString(usersMobile));
        users.setErrorCode(rs.getInt(errorCode));
        users.setMobile(rs.getString(mobile));
        users.setMessage(rs.getString(message));
        users.setUpdatedValue(rs.getString(updatedValue));
        users.setDate(rs.getTimestamp(date));
        users.setPriority(Priority.valueOf(rs.getString(priority)));
        return users;
    };

}
