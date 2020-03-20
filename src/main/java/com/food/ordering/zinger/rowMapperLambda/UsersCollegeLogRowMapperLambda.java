package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.model.UsersCollegeLogModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.UsersCollegeLogColumn.*;

public class UsersCollegeLogRowMapperLambda {
	 public static final RowMapper<UsersCollegeLogModel> usersCollegeLogRowMapperLambda = (rs, rownum) -> {
	        UsersCollegeLogModel usersCollege = new UsersCollegeLogModel();
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