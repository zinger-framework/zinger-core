package com.food.ordering.zinger.rowMapperLambda.logger;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.logger.UserCollegeLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.logger.UserCollegeLogColumn.*;

public class UserCollegeLogRowMapperLambda {
	 public static final RowMapper<UserCollegeLogModel> usersCollegeLogRowMapperLambda = (rs, rownum) -> {
	        UserCollegeLogModel usersCollege = new UserCollegeLogModel();
	        usersCollege.setUsersMobile(rs.getString(usersMobile));
	        usersCollege.setErrorCode(rs.getInt(errorCode));
	        usersCollege.setMobile(rs.getString(mobile));
	        usersCollege.setMessage(rs.getString(message));
	        usersCollege.setUpdatedValue(rs.getString(updatedValue));
	        usersCollege.setDate(rs.getTimestamp(date));
	        usersCollege.setPriority(Priority.valueOf(rs.getString(priority)));
	        return usersCollege;
	    };
}
