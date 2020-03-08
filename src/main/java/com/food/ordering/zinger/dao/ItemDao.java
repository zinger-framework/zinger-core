package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.ItemColumn;
import com.food.ordering.zinger.column.ShopColumn;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.query.ItemQuery;
import com.food.ordering.zinger.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    ShopDao shopDao;

    public Response<List<ItemModel>> getItemsByShopId(ShopModel shopModel, String oauthId, String mobile, String role) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> list = null;

        try {

            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.shopId, shopModel.getId());
            try {
                list = namedParameterJdbcTemplate.query(ItemQuery.getItemsByShopId, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null && !list.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                for (int i = 0; i < list.size(); i++)
                    list.get(i).setShopModel(shopModel);
                response.setData(list);
            }
        }
        return response;
    }

    public Response<List<ItemModel>> getItemsByName(Integer collegeId, String itemName, String oauthId, String mobile, String role) {
        Response<List<ItemModel>> response = new Response<>();
        List<ItemModel> items = null;
        try {
            if (!utilsDao.validateUser(oauthId, mobile, role).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.name, "%" + itemName + "%")
                    .addValue(ShopColumn.collegeId, collegeId);

            try {
                items = namedParameterJdbcTemplate.query(ItemQuery.getItemsByName, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (items != null && !items.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                for (int i = 0; i < items.size(); i++) {
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(items.get(i).getShopModel().getId());
                    items.get(i).setShopModel(shopModelResponse.getData());
                }
                response.setData(items);
            }
        }
        return response;
    }

    public Response<ItemModel> getItemById(Integer id) {
        ItemModel item = null;
        Response<ItemModel> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ItemColumn.id, id);
            try {
                item = namedParameterJdbcTemplate.queryForObject(ItemQuery.getItemById, parameters, ItemRowMapperLambda.itemRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (item != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                Response<ShopModel> shopModelResponse = shopDao.getShopById(item.getId());
                item.setShopModel(shopModelResponse.getData());
                response.setData(item);
            }
        }
        return response;
    }
}
