package com.food.ordering.zinger.rowMapperLambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.ShopModel;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.Column.ShopColumn.*;

public class ShopRowMapperLambda {

    public static final RowMapper<ShopModel> shopRowMapperLambda = (rs, rownum) -> {
        ShopModel shop = new ShopModel();
        shop.setId(rs.getInt(id));
        shop.setName(rs.getString(name));
        shop.setPhotoUrl(rs.getString(photoUrl));

        try {
            shop.setCoverUrls(new ObjectMapper().readValue(rs.getString(coverUrls), List.class));
        } catch (JsonProcessingException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            shop.setCoverUrls(new ArrayList<>());
        }

        shop.setMobile(rs.getString(mobile));

        PlaceModel placeModel = new PlaceModel();
        placeModel.setId(rs.getInt(placeId));
        shop.setPlaceModel(placeModel);

        shop.setOpeningTime(rs.getTime(openingTime));
        shop.setClosingTime(rs.getTime(closingTime));
        return shop;
    };
}
