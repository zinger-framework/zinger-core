package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ConfigurationColumn;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.query.ConfigurationQuery;
import com.food.ordering.zinger.rowMapperLambda.ConfigurationRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<String> insertConfiguration(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.shopId, configurationModel.getShopModel().getId())
                    .addValue(ConfigurationColumn.deliveryPrice, configurationModel.getDeliveryPrice());

            int responseResult = namedParameterJdbcTemplate.update(ConfigurationQuery.insertConfiguration, parameters);
            if (responseResult > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<ConfigurationModel> getConfigurationByShopId(ShopModel shopModel) {
        Response<ConfigurationModel> response = new Response<>();
        ConfigurationModel configurationModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.shopId, shopModel.getId());

            try {
                configurationModel = namedParameterJdbcTemplate.queryForObject(ConfigurationQuery.getConfigurationByShopId, parameters, ConfigurationRowMapperLambda.configurationRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (configurationModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                configurationModel.setShopModel(shopModel);
                response.setData(configurationModel);
            }
        }
        return response;
    }

    public Response<String> updateConfigurationModel(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;

        try {
            parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.deliveryPrice, configurationModel.getDeliveryPrice())
                    .addValue(ConfigurationColumn.isDeliveryAvailable, configurationModel.getIsDeliveryAvailable())
                    .addValue(ConfigurationColumn.isOrderTaken, configurationModel.getIsOrderTaken())
                    .addValue(ConfigurationColumn.shopId, configurationModel.getShopModel().getId());

            int responseResult = namedParameterJdbcTemplate.update(ConfigurationQuery.updateConfiguration, parameters);
            if (responseResult > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
