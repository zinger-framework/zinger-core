package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.model.UserCollegeModel;
import com.food.ordering.ssn.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.UserCollegeColumn.collegeId;
import static com.food.ordering.ssn.column.UserCollegeColumn.mobile;

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
