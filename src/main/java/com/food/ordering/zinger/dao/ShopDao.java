package com.food.ordering.zinger.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.zinger.column.ShopColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.RatingModel;
import com.food.ordering.zinger.model.ShopConfigurationModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.model.CollegeModel;
import com.food.ordering.zinger.model.logger.ShopLogModel;
import com.food.ordering.zinger.query.ShopQuery;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;

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

    public Response<String> insertShop(ShopModel shopModel,String oauthId, String mobile, String role){

        Response<String> response=new Response<>();
        MapSqlParameterSource parameters;
        ShopLogModel shopLogModel = new ShopLogModel();
        shopLogModel.setId(shopLogModel.getId());
        shopLogModel.setMobile(shopLogModel.getMobile());

        shopLogModel.setErrorCode(response.getCode());
        shopLogModel.setMessage(response.getMessage());
        shopLogModel.setUpdatedValue(shopModel.toString());
        
        try{
            if(!role.equals(UserRole.SHOP_OWNER.name())) {
                response.setCode(ErrorLog.InvalidHeader1004);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(shopModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                
                return response;
            }

            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess)){
                response.setCode(ErrorLog.InvalidHeader1005);
                response.setMessage(ErrorLog.InvalidHeader);

                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.HIGH);
                shopLogModel.setUpdatedValue(shopModel.toString());

                try {
                    auditLogDao.insertShopLog(shopLogModel);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return response;
            }

            parameters = new MapSqlParameterSource()
                                .addValue(ShopColumn.name,shopModel.getName())
                                .addValue(ShopColumn.photoUrl,shopModel.getPhotoUrl())
                                .addValue(ShopColumn.mobile,shopModel.getMobile())
                                .addValue(ShopColumn.collegeId,shopModel.getCollegeModel().getId())
                                .addValue(ShopColumn.openingTime,shopModel.getOpeningTime())
                                .addValue(ShopColumn.closingTime,shopModel.getClosingTime());

            int responseValue=namedParameterJdbcTemplate.update(ShopQuery.insertShop,parameters);
            if(responseValue>0){
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                
                shopLogModel.setErrorCode(response.getCode());
                shopLogModel.setMessage(response.getMessage());
                shopLogModel.setPriority(Priority.LOW);
                shopLogModel.setUpdatedValue(shopModel.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        try {
            auditLogDao.insertShopLog(shopLogModel);
        }
        catch (Exception e){
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
                }
                catch (Exception e){
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
        }
        catch (Exception e){
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
        }
        catch (Exception e){
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
                }
                catch (Exception e){
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
                }
                catch (Exception e){
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
                shopLogModel.setUpdatedValue(shopLogModel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            auditLogDao.insertShopLog(shopLogModel);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
