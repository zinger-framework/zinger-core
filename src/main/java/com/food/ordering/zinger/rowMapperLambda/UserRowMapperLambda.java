package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.UserColumn.*;

public class UserRowMapperLambda {
    public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        userModel.setName(rs.getString(name));
        userModel.setEmail(rs.getString(email));
        userModel.setOauthId(rs.getString(oauthId));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };
}
