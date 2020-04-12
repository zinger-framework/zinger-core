package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.model.logger.UserLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.UserLogColumn.*;

public class UserLogRowMapperLambda {
    public static final RowMapper<UserLogModel> usersLogRowMapperLambda = (rs, rownum) -> {
        UserLogModel users = new UserLogModel();
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
