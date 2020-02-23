package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.ItemModel;
import com.food.ordering.ssn.query.ItemQuery;
import com.food.ordering.ssn.rowMapperLambda.ItemRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class ItemDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	UtilsDao utilsDao;

	public Response<List<ItemModel>> getItemsByShopId(Integer shopId, String oauthId) {
		Response<List<ItemModel>> response = new Response<>();
		List<ItemModel> list = null;

		try {

			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("shop_id", shopId);

			list = jdbcTemplate.query(ItemQuery.getItemsByShopID, parameters, ItemRowMapperLambda.itemRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null && !list.isEmpty()) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(list);
			}
		}
		return response;
	}

	public Response<ItemModel> getItemById(Integer itemId, String oauthIdRh) {
		ItemModel item = null;
		Response<ItemModel> response = new Response<>();

		try {

			if (utilsDao.validateUser(oauthIdRh).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", itemId);
			item = jdbcTemplate.queryForObject(ItemQuery.getItemByID, parameters,
					ItemRowMapperLambda.itemRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (item != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(item);
			}
		}
		return response;
	}

	public Response<List<ItemModel>> getItemsByQuery(String oauthId, Integer shopId, String query) {
		Response<List<ItemModel>> response = new Response<>();
		List<ItemModel> items = null;
		try {

			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("query", query).addValue("shop_id",
					shopId);

			items = jdbcTemplate.query(ItemQuery.getItemsByQuery, parameters, ItemRowMapperLambda.itemRowMapperLambda);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (items != null && !items.isEmpty()) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(items);
			}
		}
		return response;
	}

}
