package com.food.ordering.ssn.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.LoginQuery;
import com.food.ordering.ssn.rowMapperLambda.LoginRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class UtilsDao {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public Response<UserModel> validateUser(String oauthId) {
		UserModel userModel = null;
		Response<UserModel> response = new Response<>();
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("oauth_id", oauthId);
						
			userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.validateUser, parameters, LoginRowMapperLambda.userRowMapperLambda);
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(userModel != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(userModel);
			}
		} 
		return response;
    }
}
