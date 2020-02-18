package com.food.ordering.ssn.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.LoginQuery;
import com.food.ordering.ssn.rowMapperLambda.RowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class UtilsDao {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public Response<UserModel> validateUser(String oauthId, String accessToken) {
		UserModel userModel = null;
		Response<UserModel> response = new Response<>();
		
		/* try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("oauth_id", oauthId)
					.addValue("access_token", accessToken);
			
			System.out.println("\n ");
			
			userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.validateUser, parameters, LoginRowMapperLambda.userRowMapperLambda);
			
			System.out.println("\n UserModel : " + userModel);
			
		} catch (Exception e) {
			System.out.println("\n User does not exist. Exception");
			System.out.println("\n Exception message : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(userModel != null) {
				System.out.println("\n User exists");
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(userModel);
			}else {
				System.out.println("\n User does not exist");
			}
		} */
		response.setCode(Constant.CodeSuccess);
		response.setMessage(Constant.MessageSuccess);
		response.setData(userModel);
		return response;
    }
}
