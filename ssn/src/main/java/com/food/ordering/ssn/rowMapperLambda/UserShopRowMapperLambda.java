package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.model.UserShopModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.UserShopColumn.mobile;
import static com.food.ordering.ssn.column.UserShopColumn.shopId;

public class UserShopRowMapperLambda {
    public static final RowMapper<UserShopModel> userShopRowMapperLambda = (rs, rownum) -> {
        UserShopModel userShopModel = new UserShopModel();

        UserModel userModel = new UserModel();
        userModel.setMobile(rs.getString(mobile));
        userShopModel.setUserModel(userModel);

        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        userShopModel.setShopModel(shopModel);

        return userShopModel;
    };
}
