package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.UserColumn.*;

public class UserRowMapperLambda {
    public static final RowMapper<UserModel> userLoginRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        userModel.setName(rs.getString(name));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    public static final RowMapper<UserModel> userDetailRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        userModel.setName(rs.getString(name));
        userModel.setMobile(rs.getString(mobile));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    public static final RowMapper<UserModel> userIdRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        return userModel;
    };

    public static final RowMapper<UserModel> userRoleRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    //TODO: May not be needed
    public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        userModel.setName(rs.getString(name));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };
}
