package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserShopModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.UserShopColumn.shopId;
import static com.food.ordering.zinger.constant.Column.UserShopColumn.userId;

public class UserShopRowMapperLambda {

    public static final RowMapper<UserShopModel> userShopRowMapperLambda = (rs, rownum) -> {
        UserShopModel userShopModel = new UserShopModel();

        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(userId));
        userShopModel.setUserModel(userModel);

        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        userShopModel.setShopModel(shopModel);

        return userShopModel;
    };
}
