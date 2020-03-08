package com.food.ordering.ssn.rowMapperLambda;

import com.food.ordering.ssn.model.ShopModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.ssn.column.UserShopColumn.shopId;

public class UserShopRowMapperLambda {

    public static final RowMapper<ShopModel> userShopRowMapperLambda = (rs, rownum) -> {
        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        return shopModel;
    };
}
