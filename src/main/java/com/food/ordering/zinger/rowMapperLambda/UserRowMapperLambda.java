package com.food.ordering.zinger.rowMapperLambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.zinger.constant.Column;
import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.Column.PlaceColumn.iconUrl;
import static com.food.ordering.zinger.constant.Column.UserColumn.*;
import static com.food.ordering.zinger.constant.Column.UserShopColumn.shopId;

public class UserRowMapperLambda {
    public static final RowMapper<UserModel> userLoginRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        userModel.setName(rs.getString(name));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    public static final RowMapper<UserModel> userDetailRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        userModel.setName(rs.getString(name));
        userModel.setMobile(rs.getString(mobile));
        userModel.setEmail(rs.getString(email));
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

    public static final RowMapper<UserModel> userIdRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(id));
        return userModel;
    };

    public static final RowMapper<UserModel> userRoleRowMapperLambda = (rs, rownum) -> {
        UserModel userModel = new UserModel();
        userModel.setRole(UserRole.valueOf(rs.getString(role)));
        return userModel;
    };

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

    public static final RowMapper<SellerLoginResponse> userShopDetailRowMapperLambda = (rs, rownum) -> {
        SellerLoginResponse sellerLoginResponse = new SellerLoginResponse();

        UserModel userModel = new UserModel();
        userModel.setId(rs.getInt(Column.UserColumn.id));
        userModel.setName(rs.getString(Column.UserColumn.name));
        userModel.setEmail(rs.getString(Column.UserColumn.email));
        userModel.setRole(Enums.UserRole.valueOf(rs.getString(Column.UserColumn.role)));
        sellerLoginResponse.setUserModel(userModel);

        ShopModel shopModel = new ShopModel();
        shopModel.setId(rs.getInt(shopId));
        shopModel.setName(rs.getString(Column.shopName));
        shopModel.setMobile(rs.getString(Column.shopMobile));
        shopModel.setPhotoUrl(rs.getString(Column.ShopColumn.photoUrl));
        try {
            shopModel.setCoverUrls(new ObjectMapper().readValue(rs.getString(Column.ShopColumn.coverUrls), List.class));
        } catch (JsonProcessingException e) {
            shopModel.setCoverUrls(new ArrayList<>());
        }
        shopModel.setOpeningTime(rs.getTime(Column.ShopColumn.openingTime));
        shopModel.setClosingTime(rs.getTime(Column.ShopColumn.closingTime));
        shopModel.setPlaceModel(null);
        sellerLoginResponse.setShopModel(shopModel);

        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setShopModel(null);
        configurationModel.setMerchantId(rs.getString(Column.ConfigurationColumn.merchantId));
        configurationModel.setDeliveryPrice(rs.getDouble(Column.ConfigurationColumn.deliveryPrice));
        configurationModel.setIsOrderTaken(rs.getInt(Column.ConfigurationColumn.isOrderTaken));
        configurationModel.setIsDeliveryAvailable(rs.getInt(Column.ConfigurationColumn.isDeliveryAvailable));
        sellerLoginResponse.setConfigurationModel(configurationModel);

        RatingModel ratingModel = new RatingModel();
        ratingModel.setShopModel(null);
        ratingModel.setRating(rs.getDouble(Column.RatingColumn.rating));
        ratingModel.setUserCount(rs.getInt(Column.RatingColumn.userCount));
        sellerLoginResponse.setRatingModel(ratingModel);

        return sellerLoginResponse;
    };
}
