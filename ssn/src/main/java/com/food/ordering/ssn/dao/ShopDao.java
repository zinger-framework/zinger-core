package com.food.ordering.ssn.dao;

import java.util.List;

import com.food.ordering.ssn.column.ShopColumn;
import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.query.ItemQuery;
import com.food.ordering.ssn.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.query.ShopQuery;
import com.food.ordering.ssn.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.ssn.utils.Response;

@Repository
public class ShopDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<List<ShopModel>> getShopsByCollegeId(Integer collegeId, String oauthId, String mobileNo) {
        Response<List<ShopModel>> response = new Response<>();
        List<ShopModel> list = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobileNo).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue(ShopColumn.collegeId, collegeId);
            list = namedParameterJdbcTemplate.query(ShopQuery.getShopByCollegeId, parameters, ShopRowMapperLambda.shopRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
        return response;
    }

    public Response<ShopModel> getShopById(Integer shopId, String oauthId, String mobileNo) {
        Response<ShopModel> response = new Response<>();
        ShopModel shopModel = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobileNo).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ShopColumn.id, shopId);
            shopModel = namedParameterJdbcTemplate.queryForObject(ShopQuery.getShopById, parameters, ShopRowMapperLambda.shopRowMapperLambda);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shopModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(shopModel);
            }
        }
        return response;
    }

    public Response<List<ItemModel>> getMenuByShopId(Integer shopId, String oauthId, String mobileNo) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobileNo).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue(ShopColumn.id, shopId);
            list = namedParameterJdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
        return response;
    }
}
