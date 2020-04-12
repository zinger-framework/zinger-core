package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.model.PlaceModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.PlaceColumn.*;

public class PlaceRowMapperLambda {
    public static final RowMapper<PlaceModel> collegeRowMapperLambda = (rs, rownum) -> {
        PlaceModel place = new PlaceModel();
        place.setId(rs.getInt(id));
        place.setName(rs.getString(name));
        place.setIconUrl(rs.getString(iconUrl));
        place.setAddress(rs.getString(address));
        return place;
    };
}
