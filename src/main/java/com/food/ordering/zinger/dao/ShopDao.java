package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ShopColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import com.food.ordering.zinger.query.ShopQuery;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public Response<String> insertShop(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {

        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;
        ShopLogModel shopLogModel = new ShopLogModel();
        shopLogModel.setId(shopLogModel.getId());
        shopLogModel.setMobile(shopLogModel.getMobile());

        shopLogModel.setErrorCode(response.getCode());
        shopLogModel.setMessage(response.getMessage());
        shopLogModel.setUpdatedValue(configurationModel.toString());

        try {
            if (!role.equals(UserRole.SHOP_OWNER.name())) {
                response.setCode(ErrorLog.InvalidHeader1004);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(configurationModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.InvalidHeader1005);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(configurationModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            ShopModel shopModel = configurationModel.getShopModel();
            parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.name, shopModel.getName())
                    .addValue(ShopColumn.photoUrl, shopModel.getPhotoUrl())
                    .addValue(ShopColumn.mobile, shopModel.getMobile())
                    .addValue(ShopColumn.collegeId, shopModel.getCollegeModel().getId())
                    .addValue(ShopColumn.openingTime, shopModel.getOpeningTime())
                    .addValue(ShopColumn.closingTime, shopModel.getClosingTime())
                    .addValue(ShopColumn.isDelete, shopModel.getIsDelete());

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
            simpleJdbcInsert
                    .withTableName(ShopColumn.tableName)
                    .usingGeneratedKeyColumns(ShopColumn.id);

            Number responseValue = simpleJdbcInsert.executeAndReturnKey(parameters);
            configurationModel.getShopModel().setId(responseValue.intValue());

            //int responseValue = namedParameterJdbcTemplate.update(ShopQuery.insertShop, parameters);
            Response<String> configurationModelResponse = configurationDao.insertConfiguration(configurationModel);

            if (responseValue.intValue() > 0 && configurationModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.LOW);
                shopLogModel.setUpdatedValue(shopModel.toString());
            }
            else if(responseValue.intValue() <= 0)
                response.setData(ErrorLog.ShopDetailNotUpdated);
            else
                response.setData(ErrorLog.ConfigurationDetailNotUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            auditLogDao.insertShopLog(shopLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<List<ShopConfigurationModel>> getShopsByCollegeId(Integer collegeId, String oauthId, String mobile, String role) {
        Response<List<ShopConfigurationModel>> response = new Response<>();
        List<ShopModel> list = null;
        List<ShopConfigurationModel> shopConfigurationModelList = null;
        ShopLogModel shopLogModel = new ShopLogModel();
        shopLogModel.setId(shopLogModel.getId());
        shopLogModel.setMobile(mobile);

        shopLogModel.setErrorCode(response.getCode());
        shopLogModel.setMessage(response.getMessage());
        shopLogModel.setUpdatedValue(mobile.toString());

        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.InvalidHeader1006);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(mobile.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.collegeId, collegeId);

            try {
                list = namedParameterJdbcTemplate.query(ShopQuery.getShopByCollegeId, parameters, ShopRowMapperLambda.shopRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                shopConfigurationModelList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCollegeModel(null);

                    Response<ShopModel> shopModelResponse = getShopById(list.get(i).getId());
                    Response<RatingModel> ratingModelResponse = ratingDao.getRatingByShopId(list.get(i));
                    Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(list.get(i));

                    ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();
                    shopConfigurationModel.setShopModel(shopModelResponse.getData());
                    shopConfigurationModel.setConfigurationModel(configurationModelResponse.getData());
                    shopConfigurationModel.setRatingModel(ratingModelResponse.getData());
                    shopConfigurationModelList.add(shopConfigurationModel);
                }

                response.setData(shopConfigurationModelList);
            }
        }
        try {
            auditLogDao.insertShopLog(shopLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<ShopModel> getShopById(Integer shopId) {
        Response<ShopModel> response = new Response<>();
        ShopModel shopModel = null;
        ShopLogModel shopLogModel = new ShopLogModel();

        shopLogModel.setId(shopLogModel.getId());
        shopLogModel.setErrorCode(response.getCode());
        shopLogModel.setMessage(response.getMessage());
        shopLogModel.setUpdatedValue(shopId.toString());


        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.id, shopId);

            try {
                shopModel = namedParameterJdbcTemplate.queryForObject(ShopQuery.getShopById, parameters, ShopRowMapperLambda.shopRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shopModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                if (shopModel.getCollegeModel().getName() == null || shopModel.getCollegeModel().getName().isEmpty()) {
                    Response<CollegeModel> collegeModelResponse = collegeDao.getCollegeById(shopModel.getCollegeModel().getId());
                    shopModel.setCollegeModel(collegeModelResponse.getData());
                }
                response.setData(shopModel);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.LOW);
                shopLogModel.setUpdatedValue(shopId.toString());
            }
        }
        try {
            auditLogDao.insertShopLog(shopLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> updateShopConfigurationModel(ConfigurationModel configurationModel, String oauthId, String mobile, String role) {
        Response<String> response = new Response<>();
        MapSqlParameterSource parameters;
        ShopLogModel shopLogModel = new ShopLogModel();
        shopLogModel.setId(shopLogModel.getId());
        shopLogModel.setMobile(mobile);

        shopLogModel.setErrorCode(response.getCode());
        shopLogModel.setMessage(response.getMessage());
        shopLogModel.setUpdatedValue(shopLogModel.toString());

        try {
            if (!role.equals((UserRole.SHOP_OWNER).name())) {
                response.setCode(ErrorLog.InvalidHeader1007);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(shopLogModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.InvalidHeader1008);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(shopLogModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

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

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.LOW);
                shopLogModel.setUpdatedValue(configurationModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertShopLog(shopLogModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
