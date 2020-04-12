package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column.ShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import com.food.ordering.zinger.constant.Query.ShopQuery;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ShopDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    ConfigurationDao configurationDao;

    @Autowired
    RatingDao ratingDao;

    @Autowired
    CollegeDao collegeDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<String> insertShop(ConfigurationModel configurationModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;
        Priority priority = Priority.MEDIUM;

        try {
            if (!requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                response.setCode(ErrorLog.IH1004);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1005);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                ShopModel shopModel = configurationModel.getShopModel();
                parameters = new MapSqlParameterSource()
                        .addValue(ShopColumn.name, shopModel.getName())
                        .addValue(ShopColumn.photoUrl, shopModel.getPhotoUrl())
                        .addValue(ShopColumn.mobile, shopModel.getMobile())
                        .addValue(ShopColumn.collegeId, shopModel.getCollegeModel().getId())
                        .addValue(ShopColumn.openingTime, shopModel.getOpeningTime())
                        .addValue(ShopColumn.closingTime, shopModel.getClosingTime())
                        .addValue(ShopColumn.isDelete, 0);

                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
                simpleJdbcInsert.withTableName(ShopColumn.tableName).usingGeneratedKeyColumns(ShopColumn.id);
                Number responseValue = simpleJdbcInsert.executeAndReturnKey(parameters);

                configurationModel.getShopModel().setId(responseValue.intValue());
                Response<String> configurationModelResponse = configurationDao.insertConfiguration(configurationModel);

                if (responseValue.intValue() > 0 && configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else if (responseValue.intValue() <= 0) {
                    priority = Priority.HIGH;
                    response.setCode(ErrorLog.SDNU1251);
                    response.setData(ErrorLog.ShopDetailNotUpdated);
                } else {
                    response.setCode(ErrorLog.CDNU1252);
                    response.setData(ErrorLog.ConfigurationDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1253);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertShopLog(new ShopLogModel(response, requestHeaderModel.getMobile(), null, configurationModel.toString(), priority));
        return response;
    }

    public Response<List<ShopConfigurationModel>> getShopsByCollegeId(Integer collegeId, RequestHeaderModel requestHeaderModel) {
        Response<List<ShopConfigurationModel>> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        List<ShopModel> list = null;
        List<ShopConfigurationModel> shopConfigurationModelList = null;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1006);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(ShopColumn.collegeId, collegeId);

                try {
                    list = namedParameterJdbcTemplate.query(ShopQuery.getShopByCollegeId, parameters, ShopRowMapperLambda.shopRowMapperLambda);
                } catch (Exception e) {
                    response.setCode(ErrorLog.CE1254);
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1255);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (list != null && !list.isEmpty()) {
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                shopConfigurationModelList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCollegeModel(null);

                    Response<ShopModel> shopModelResponse = getShopById(list.get(i).getId());
                    Response<RatingModel> ratingModelResponse = ratingDao.getRatingByShopId(list.get(i));
                    Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(list.get(i));

                    ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();
                    shopModelResponse.getData().setCollegeModel(null);
                    ratingModelResponse.getData().setShopModel(null);
                    configurationModelResponse.getData().setShopModel(null);

                    if (shopModelResponse.getCode().equals(ErrorLog.CodeSuccess) && shopModelResponse.getMessage().equals(ErrorLog.Success)) {
                        shopConfigurationModel.setShopModel(shopModelResponse.getData());
                    } else {
                        priority = Priority.HIGH;
                        response.setCode(ErrorLog.SDNA1256);
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                    }

                    if (configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess) && configurationModelResponse.getMessage().equals(ErrorLog.Success))
                        shopConfigurationModel.setConfigurationModel(configurationModelResponse.getData());
                    else {
                        priority = Priority.HIGH;
                        response.setCode(ErrorLog.CDNA1257);
                        response.setMessage(ErrorLog.ConfigurationDetailNotAvailable);
                    }

                    if (ratingModelResponse.getCode().equals(ErrorLog.CodeSuccess) && ratingModelResponse.getMessage().equals(ErrorLog.Success))
                        shopConfigurationModel.setRatingModel(ratingModelResponse.getData());
                    else {
                        priority = Priority.HIGH;
                        response.setCode(ErrorLog.SDNA1258);
                        response.setMessage(ErrorLog.ShopDetailNotAvailable);
                    }

                    shopConfigurationModelList.add(shopConfigurationModel);
                }
                response.setData(shopConfigurationModelList);
            }
        }

        auditLogDao.insertShopLog(new ShopLogModel(response, requestHeaderModel.getMobile(), null, collegeId.toString(), priority));
        return response;
    }

    public Response<ShopModel> getShopById(Integer shopId) {
        Response<ShopModel> response = new Response<>();
        ShopModel shopModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.id, shopId);

            try {
                shopModel = namedParameterJdbcTemplate.queryForObject(ShopQuery.getShopById, parameters, ShopRowMapperLambda.shopRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (shopModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                if (shopModel.getCollegeModel().getName() == null || shopModel.getCollegeModel().getName().isEmpty()) {
                    Response<CollegeModel> collegeModelResponse = collegeDao.getCollegeById(shopModel.getCollegeModel().getId());
                    shopModel.setCollegeModel(collegeModelResponse.getData());
                }
                response.setData(shopModel);
            }
        }
        return response;
    }

    public Response<String> updateShopConfigurationModel(ConfigurationModel configurationModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;
        Priority priority = Priority.MEDIUM;

        try {
            if (!requestHeaderModel.getRole().equals((UserRole.SHOP_OWNER).name())) {
                response.setCode(ErrorLog.IH1007);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1008);
                response.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                Response<String> configResponse = configurationDao.updateConfigurationModel(configurationModel);

                parameters = new MapSqlParameterSource()
                        .addValue(ShopColumn.name, configurationModel.getShopModel().getName())
                        .addValue(ShopColumn.photoUrl, configurationModel.getShopModel().getPhotoUrl())
                        .addValue(ShopColumn.mobile, configurationModel.getShopModel().getMobile())
                        .addValue(ShopColumn.openingTime, configurationModel.getShopModel().getOpeningTime())
                        .addValue(ShopColumn.closingTime, configurationModel.getShopModel().getClosingTime())
                        .addValue(ShopColumn.id, configurationModel.getShopModel().getId());

                int responseResult = namedParameterJdbcTemplate.update(ShopQuery.updateShop, parameters);
                if (responseResult > 0 || configResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                    priority = Priority.LOW;
                } else {
                    response.setCode(ErrorLog.CDNU1260);
                    response.setMessage(ErrorLog.ConfigurationDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            response.setCode(ErrorLog.CE1259);
        }

        auditLogDao.insertShopLog(new ShopLogModel(response, requestHeaderModel.getMobile(), configurationModel.getShopModel().getId(), configurationModel.toString(), priority));
        return response;
    }
}
