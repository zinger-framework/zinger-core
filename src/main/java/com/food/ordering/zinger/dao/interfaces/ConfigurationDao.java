package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.ConfigurationColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ConfigurationQuery;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.rowMapperLambda.ConfigurationRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

public interface ConfigurationDao {
    Response<String> insertConfiguration(ConfigurationModel configurationModel);

    Response<ConfigurationModel> getConfigurationByShopId(ShopModel shopModel);

    Response<String> updateConfigurationModel(ConfigurationModel configurationModel);
}
