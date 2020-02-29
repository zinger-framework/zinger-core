package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.query.ShopQuery;
import com.food.ordering.ssn.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class ShopDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	UtilsDao utilsDao;
	
	public Response<List<ShopModel>> getShopsByCollegeId(Integer collegeId, String oauthId) {
		Response<List<ShopModel>> response = new Response<>();
		List<ShopModel> list = null;

		try {
			
			if(utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;
			
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("college_id", collegeId);

			list = jdbcTemplate.query(ShopQuery.getShopsByCollegeID, parameters, ShopRowMapperLambda.shopRowMapperLambda);
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
}
