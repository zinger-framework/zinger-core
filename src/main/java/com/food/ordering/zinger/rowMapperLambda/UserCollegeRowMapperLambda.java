package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.UserCollegeModel;
import com.food.ordering.zinger.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.column.UserCollegeColumn.collegeId;
import static com.food.ordering.zinger.column.UserCollegeColumn.mobile;

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
