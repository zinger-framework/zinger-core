package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.query.ShopQuery;
import com.food.ordering.ssn.rowMapperLambda.RowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class ShopDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	public Response<ShopModel> createShop(ShopModel shop, String oauthId, String accessToken) {
		Response<ShopModel> response = new Response<>();

		try {
			
			if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", shop.getName())
					.addValue("mobile", shop.getMobile()).addValue("college_id", shop.getCollegeId())
					.addValue("opening_time", shop.getOpeningTime()).addValue("photo_url", shop.getPhotoUrl())
					.addValue("closing_time", shop.getClosingTime());

			jdbcTemplate.update(ShopQuery.insertShop, parameters);

			shop.setIsDelete(0);

			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(shop);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Response<List<ShopModel>> getAllShops(String oauthId, String accessToken) {
		Response<List<ShopModel>> response = new Response<>();
		List<ShopModel> list = null;

		try {
			
			if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			list = jdbcTemplate.query(ShopQuery.getAllShops, RowMapperLambda.shopRowMapperLambda);
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
	
	public Response<List<ShopModel>> getShopsByCollegeId(Integer collegeId, String oauthId, String accessToken) {
		Response<List<ShopModel>> response = new Response<>();
		List<ShopModel> list = null;

		try {
			
			if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("college_id", collegeId);

			list = jdbcTemplate.query(ShopQuery.getShopsByCollegeID, parameters, RowMapperLambda.shopRowMapperLambda);
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

	public Response<ShopModel> getShopById(Integer shopId, String oauthIdRh, String accessToken) {
		ShopModel shop = null;
		Response<ShopModel> response = new Response<>();

		try {
			
			if(new UtilsDao().validateUser(oauthIdRh, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", shopId);
			shop = jdbcTemplate.queryForObject(ShopQuery.getShopByID, parameters,
					RowMapperLambda.shopRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (shop != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(shop);
			}
		}
		return response;
	}

	public Response<ShopModel> updateShopById(ShopModel shop, String oauthId, String accessToken) {
		Response<ShopModel> response = new Response<>();

		try {
			
			if(new UtilsDao().validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", shop.getID())
					.addValue("name", shop.getName()).addValue("college_id", shop.getCollegeId())
					.addValue("mobile", shop.getMobile()).addValue("opening_time", shop.getOpeningTime())
					.addValue("closing_time", shop.getClosingTime()).addValue("photo_url", shop.getPhotoUrl());

			jdbcTemplate.update(ShopQuery.updateShopByID, parameters);

			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(shop);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public Response<ShopModel> deleteShopById(Integer shopId, String oauthIdRh, String accessToken) {
		Response<ShopModel> response = new Response<>();

		try {
			
			if(new UtilsDao().validateUser(oauthIdRh, accessToken).getCode() != Constant.CodeSuccess)
				return response;
			
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", shopId);

			jdbcTemplate.update(ShopQuery.deleteShopByID, parameters);

			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}
