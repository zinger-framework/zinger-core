package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.ShopQuery;
import com.food.ordering.zinger.dao.interfaces.ConfigurationDao;
import com.food.ordering.zinger.dao.interfaces.ShopDao;
import com.food.ordering.zinger.exception.GenericException;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.zinger.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.ShopDetailNotDeleted;

/**
 * ShopDao is responsible for CRUD operations in
 * Shop table in MySQL.
 *
 * @implNote Request Header (RH) parameter is sent in all endpoints
 * to avoid unauthorized access to our service.
 * @implNote Please check the Rating and Configuration table for better understanding.
 * @implNote All endpoint services are audited for both success and error responses
 * using "AuditLogDao".
 * <p>
 * Endpoints starting with "/shop" invoked here.
 */
@Repository
@Transactional
public class ShopDaoImpl implements ShopDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    ConfigurationDao configurationDao;

    /**
     * Inserts the shop details.
     * Authorized by SUPER_ADMIN only.
     * <p>
     * Insert the shop details in the Shop table
     * Insert the shop configuration details in the Configuration table
     *
     * @param configurationModel ConfigurationModel
     * @return success response if both the insertion is successful.
     */
    @Override
    public Response<String> insertShop(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();

        ShopModel shopModel = configurationModel.getShopModel();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ShopColumn.name, shopModel.getName())
                .addValue(ShopColumn.photoUrl, shopModel.getPhotoUrl())
                .addValue(ShopColumn.coverUrls, Helper.toJsonFormattedString(shopModel.getCoverUrls()))
                .addValue(ShopColumn.mobile, shopModel.getMobile())
                .addValue(ShopColumn.placeId, shopModel.getPlaceModel().getId())
                .addValue(ShopColumn.openingTime, shopModel.getOpeningTime())
                .addValue(ShopColumn.closingTime, shopModel.getClosingTime())
                .addValue(ShopColumn.isDelete, 0);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
        simpleJdbcInsert.withTableName(ShopColumn.tableName).usingGeneratedKeyColumns(ShopColumn.id);
        Number responseValue = simpleJdbcInsert.executeAndReturnKey(parameters);

        if (responseValue.intValue() > 0) {
            configurationModel.getShopModel().setId(responseValue.intValue());
            Response<String> configurationModelResponse = configurationDao.insertConfiguration(configurationModel);

            if(configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess)){
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            }
            else{
                response.prioritySet(Priority.HIGH);
                response.setCode(ErrorLog.CDNU1252);
                response.setMessage(ErrorLog.ConfigurationDetailNotUpdated);
                throw new GenericException(response);
            }
        }
        else{
            response.prioritySet(Priority.HIGH);
            response.setCode(ErrorLog.SDNU1251);
            response.setMessage(ErrorLog.ShopDetailNotUpdated);
            throw new GenericException(response);
        }

        return response;
    }

    /**
     * Gets list of shops by place id.
     *
     * @param placeId Integer
     * @return the details along with the configuration
     * of the list of shops for the given place id.
     */
    @Override
    public Response<List<ShopConfigurationModel>> getShopsByPlaceId(Integer placeId) {
        Response<List<ShopConfigurationModel>> response = new Response<>();
        List<ShopConfigurationModel> shopConfigurationModelList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.placeId, placeId);
            shopConfigurationModelList = namedParameterJdbcTemplate.query(ShopQuery.getShopByPlaceId, parameters, ShopRowMapperLambda.shopRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.SDNA1256);
            response.setMessage(ErrorLog.ShopDetailNotAvailable);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (shopConfigurationModelList != null) {
                response.prioritySet(Priority.LOW);
                response.setCode(shopConfigurationModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(shopConfigurationModelList);
            }
        }
        return response;
    }

    /**
     * Gets shop by id.
     *
     * @param shopId Integer
     * @return the details of the shop.
     */
    public Response<ShopConfigurationModel> getShopById(Integer shopId) {
        Response<ShopConfigurationModel> response = new Response<>();
        ShopConfigurationModel shopConfigurationModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.id, shopId);
            shopConfigurationModel = namedParameterJdbcTemplate.queryForObject(ShopQuery.getShopById, parameters, ShopRowMapperLambda.shopRowMapperLambda);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (shopConfigurationModel != null) {
                response.prioritySet(Priority.LOW);
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(shopConfigurationModel);
            }
        }

        return response;
    }

    /**
     * Updates the shop details and configuration for the given shop.
     * Authorized by SHOP_OWNER only.
     *
     * @param configurationModel ConfigurationModel
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateShopConfigurationModel(ConfigurationModel configurationModel) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;

        try {
            Response<String> configResponse = configurationDao.updateConfigurationModel(configurationModel);

            parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.name, configurationModel.getShopModel().getName())
                    .addValue(ShopColumn.photoUrl, configurationModel.getShopModel().getPhotoUrl())
                    .addValue(ShopColumn.coverUrls, Helper.toJsonFormattedString(configurationModel.getShopModel().getCoverUrls()))
                    .addValue(ShopColumn.mobile, configurationModel.getShopModel().getMobile())
                    .addValue(ShopColumn.openingTime, configurationModel.getShopModel().getOpeningTime())
                    .addValue(ShopColumn.closingTime, configurationModel.getShopModel().getClosingTime())
                    .addValue(ShopColumn.id, configurationModel.getShopModel().getId());

            int responseResult = namedParameterJdbcTemplate.update(ShopQuery.updateShop, parameters);
            if (responseResult > 0 || configResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(ErrorLog.CDNU1260);
                response.setMessage(ErrorLog.ConfigurationDetailNotUpdated);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            response.setCode(ErrorLog.CE1259);
        }

        return response;
    }

    @Override
    public Response<String> deleteShopById(Integer shopId) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.id, shopId);

            int responseValue = namedParameterJdbcTemplate.update(ShopQuery.deleteShop, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(ErrorLog.SDND1257);
                response.setMessage(ShopDetailNotDeleted);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1258);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }
}
