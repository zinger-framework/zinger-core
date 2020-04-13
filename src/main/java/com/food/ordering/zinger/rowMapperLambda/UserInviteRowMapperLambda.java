package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.model.UserInviteModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.UserInviteColumn.*;

public class UserInviteRowMapperLambda {

    public static final RowMapper<UserInviteModel> sellerInviteModelRowMapper = (rs, rownum) -> {
        UserInviteModel userInviteModel = new UserInviteModel();

        ShopModel shopModel  = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        userInviteModel.setShopModel(shopModel);

        UserModel userModel  = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        userModel.setRole(Enums.UserRole.valueOf(rs.getString(role)));
        userInviteModel.setUserModel(userModel);

        userInviteModel.setDate(rs.getTimestamp(invitedAt));

        return userInviteModel;
    };
}
