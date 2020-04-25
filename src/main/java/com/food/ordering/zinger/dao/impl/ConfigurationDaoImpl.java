package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.ConfigurationColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ConfigurationQuery;
import com.food.ordering.zinger.dao.interfaces.ConfigurationDao;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.rowMapperLambda.ConfigurationRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * ConfigurationDao is responsible for CRUD operations in
 * Configuration table in MySQL.
 *
 * @implNote Please check the Shop table for better understanding.
 */
@Repository
public class ConfigurationDaoImpl implements ConfigurationDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Insert the configuration details for the given shop
     *
     * @param configurationModel ConfigurationModel
     * @return success response if the insertion is successful.
     */
    @Override
    public Response<String> insertConfiguration(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.shopId, configurationModel.getShopModel().getId())
                    .addValue(ConfigurationColumn.merchantId, configurationModel.getMerchantId())
                    .addValue(ConfigurationColumn.deliveryPrice, configurationModel.getDeliveryPrice());

            int responseResult = namedParameterJdbcTemplate.update(ConfigurationQuery.insertConfiguration, parameters);
            if (responseResult > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Gets configuration by shop id.
     *
     * @param shopModel ShopModel
     * @return the details of the shop configuration for the given shop.
     */
    @Override
    public Response<ConfigurationModel> getConfigurationByShopId(ShopModel shopModel) {
        Response<ConfigurationModel> response = new Response<>();
        ConfigurationModel configurationModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.shopId, shopModel.getId());

            try {
                configurationModel = namedParameterJdbcTemplate.queryForObject(ConfigurationQuery.getConfigurationByShopId, parameters, ConfigurationRowMapperLambda.configurationRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
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

    /**
     * Updates the shop configuration for the given shop.
     *
     * @param configurationModel ConfigurationModel
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateConfigurationModel(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;

        try {
            parameters = new MapSqlParameterSource()
                    .addValue(ConfigurationColumn.merchantId, configurationModel.getMerchantId())
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }
}
