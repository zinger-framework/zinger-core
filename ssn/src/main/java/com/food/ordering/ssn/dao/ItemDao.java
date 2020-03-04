package com.food.ordering.ssn.dao;

import java.util.List;

import com.food.ordering.ssn.column.CollegeColumn;
import com.food.ordering.ssn.column.ItemColumn;
import com.food.ordering.ssn.column.ShopColumn;
import com.food.ordering.ssn.utils.ErrorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.query.ItemQuery;
import com.food.ordering.ssn.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.ssn.utils.Response;

@Repository
public class ItemDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    public Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId, String mobile) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;

        try {

            if (utilsDao.validateUser(oauthId, mobile).getCode() != ErrorLog.CodeSuccess)
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue(ItemColumn.shopId, shopId);
            list = jdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemRowMapperLambda);

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

    public Response<ItemModel> getItemById(Integer itemId, String oauthIdRh, String mobile) {
        ItemModel item = null;
        Response<ItemModel> response = new Response<>();

        try {

            if (utilsDao.validateUser(oauthIdRh, mobile).getCode() != ErrorLog.CodeSuccess)
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue(ItemColumn.id, itemId);
            item = jdbcTemplate.queryForObject(ItemQuery.getItemById, parameters, ItemRowMapperLambda.itemRowMapperLambda);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (item != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(item);
            }
        }
        return response;
    }

    public Response<List<ItemModel>> getItemsByName(Integer collegeId, String itemName, String oauthId, String mobile) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> items = null;
        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue(ItemColumn.name, itemName)
                    .addValue(ShopColumn.collegeId, collegeId);

            items = jdbcTemplate.query(ItemQuery.getItemsByName, parameters, ItemRowMapperLambda.itemRowMapperLambda);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (items != null && !items.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(items);
            }
        }
        return response;
    }

}
