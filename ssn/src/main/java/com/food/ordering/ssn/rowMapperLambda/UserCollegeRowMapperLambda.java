package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.*;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.UserCollegeColumn.*;

public class UserCollegeRowMapperLambda {
	public static final RowMapper<UserCollegeModel> userCollegeRowMapperLambda = (rs, rownum) -> {
		UserCollegeModel userCollegeModel = new UserCollegeModel();

		UserModel userModel = new UserModel();
		userModel.setMobile(rs.getString(mobile));
		userCollegeModel.setUserModel(userModel);

		CollegeModel collegeModel = new CollegeModel();
		collegeModel.setId(rs.getInt(collegeId));
		userCollegeModel.setCollegeModel(collegeModel);

		return userCollegeModel;
	};
}
