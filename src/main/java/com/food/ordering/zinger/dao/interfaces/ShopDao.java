package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ShopQuery;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public interface ShopDao {
    Response<String> insertShop(ConfigurationModel configurationModel, RequestHeaderModel requestHeaderModel);

    public Response<List<ShopConfigurationModel>> getShopsByPlaceId(Integer placeId, RequestHeaderModel requestHeaderModel);

    public Response<ShopModel> getShopById(Integer shopId);

    public Response<String> updateShopConfigurationModel(ConfigurationModel configurationModel, RequestHeaderModel requestHeaderModel);
}
