package com.food.ordering.zinger.rowMapperLambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.utils.Helper;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.Column.UserColumn.*;

public class UserRowMapperLambda {
    public static final RowMapper<UserModel> userIdRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        userModel.setName(rs.getString(name));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    public static final RowMapper<UserModel> userRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        if(Helper.isNotNull(rs.getString(name)))
            userModel.setName(rs.getString(name));
        if(Helper.isNotNull(rs.getString(email)))
            userModel.setEmail(rs.getString(email));
        if(Helper.isNotNull(rs.getString(email)))
            userModel.setOauthId(rs.getString(oauthId));

        try {
            userModel.setNotificationToken(new ObjectMapper().readValue(rs.getString(notifToken), List.class));
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            userModel.setNotificationToken(new ArrayList<>());
        }

        if(Helper.isNotNull(rs.getString(role)))
            userModel.setRole(UserRole.valueOf(rs.getString(role)));

        return userModel;
    };
}
