package com.food.ordering.zinger.rowMapperLambda;

import com.food.ordering.zinger.constant.Column;
import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.model.PlaceModel;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserPlaceModel;
import org.springframework.jdbc.core.RowMapper;

import static com.food.ordering.zinger.constant.Column.PlaceColumn.iconUrl;
import static com.food.ordering.zinger.constant.Column.UserColumn.*;

public class UserPlaceRowMapperLambda {

    public static final RowMapper<UserPlaceModel> userPlaceRowMapperLambda = (rs, rownum) -> {
        UserPlaceModel userPlaceModel = new UserPlaceModel();

        try {
            UserModel userModel = new UserModel();
            userModel.setId(rs.getInt(id));
            userModel.setName(rs.getString(name));
            userModel.setEmail(rs.getString(email));
            userModel.setRole(Enums.UserRole.valueOf(rs.getString(role)));
            userPlaceModel.setUserModel(userModel);
        } catch (Exception e) {
        }

        try {
            PlaceModel placeModel = new PlaceModel();
            placeModel.setId(rs.getInt(Column.UserPlaceColumn.placeId));
            placeModel.setName(rs.getString(Column.placeName));
            placeModel.setIconUrl(rs.getString(iconUrl));
            placeModel.setAddress(rs.getString(Column.placeAddress));
            userPlaceModel.setPlaceModel(placeModel);
        } catch (Exception e) {
        }

        return userPlaceModel;
    };
}
