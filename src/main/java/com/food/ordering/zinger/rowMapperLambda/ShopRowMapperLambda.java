package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.ShopModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.ShopColumn.*;

public class ShopRowMapperLambda {

    public static final RowMapper<ShopModel> shopRowMapperLambda = (rs, rownum) -> {
        ShopModel shop = new ShopModel();
        shop.setId(rs.getInt(id));
        shop.setName(rs.getString(name));
        shop.setPhotoUrl(rs.getString(photoUrl));
        shop.setMobile(rs.getString(mobile));

        PlaceModel placeModel = new PlaceModel();
        placeModel.setId(rs.getInt(placeId));
        shop.setPlaceModel(placeModel);

        shop.setOpeningTime(rs.getTime(openingTime));
        shop.setClosingTime(rs.getTime(closingTime));
        return shop;
    };
}
