package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ShopColumn;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.query.ShopQuery;
import com.food.ordering.zinger.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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

    public Response<List<ShopConfigurationModel>> getShopsByCollegeId(CollegeModel collegeModel, String oauthId, String mobile, String role) {
        Response<List<ShopConfigurationModel>> response = new Response<>();
        List<ShopModel> list = null;
        List<ShopConfigurationModel> shopConfigurationModelList = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.collegeId, collegeModel.getId());
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
                    list.get(i).setCollegeModel(collegeModel);

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
            }
        }
        return response;
    }
}
