package com.food.ordering.ssn.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;

import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.query.LoginQuery;
import com.food.ordering.ssn.rowMapperLambda.LoginRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Response;

@Repository
public class LoginDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public Response<UserModel> insertUser(UserModel user, String oauthId, String accessToken) {
		Response<UserModel> response = new Response<>();
		
		if(validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("name", user.getName())
					.addValue("email", user.getEmail())
					.addValue("mobile", user.getMobile())
					.addValue("oauth_id", user.getOauthId())
					.addValue("access_token", user.getAccessToken())
					.addValue("role", user.getRole());
	
			namedParameterJdbcTemplate.update(LoginQuery.insertUser, parameters);
			user.setIsDelete(0);
			
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(user);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

	public Response<List<UserModel>> getAllUser(String oauthId, String accessToken) {
		Response<List<UserModel>> response = new Response<>();
		List<UserModel> list = null;
		
		if(validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			list = jdbcTemplate.query(LoginQuery.getAllUser, LoginRowMapperLambda.userRowMapperLambda);
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(list != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(list);
			}
		}
		return response;
	}
	
	public Response<UserModel> getUserByOauthId(String oauthId,String oauthIdRh, String accessToken) {
		UserModel userModel = null;
		Response<UserModel> response = new Response<>();
		
		if(validateUser(oauthIdRh, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", oauthId);
			userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserByOauthId, parameters, LoginRowMapperLambda.userRowMapperLambda);
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

	public Response<UserModel> updateUserByOauthId(UserModel user,String oauthId, String accessToken) {
		Response<UserModel> response = new Response<>();
		
		if(validateUser(oauthId, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("name", user.getName())
					.addValue("email", user.getEmail())
					.addValue("mobile", user.getMobile())
					.addValue("oauth_id", user.getOauthId())
					.addValue("access_token", user.getAccessToken())
					.addValue("role", user.getRole());
	
			namedParameterJdbcTemplate.update(LoginQuery.updateUserByOauthId, parameters);
			
			response.setCode(Constant.CodeSuccess);	
			response.setMessage(Constant.MessageSuccess);
			response.setData(user);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public Response<UserModel> deleteUserByOauthId(String oauthId,String oauthIdRh, String accessToken) {
		Response<UserModel> response = new Response<>();
		
		if(validateUser(oauthIdRh, accessToken).getCode() != Constant.CodeSuccess)
			return response;
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("oauth_id", oauthId);
			namedParameterJdbcTemplate.update(LoginQuery.deleteUserByOauthId, parameters);
			
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public Response<UserModel> validateUser(String oauthId, String accessToken) {
		UserModel userModel = null;
		Response<UserModel> response = new Response<>();
		
		try {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("oauth_id", oauthId)
					.addValue("access_token", accessToken);
			
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

/*public Response<UserModel> getUserById(Integer id) {
UserModel userModel = null;
Response<UserModel> response = new Response<>();

try {
	SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
	userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserById, parameters, LoginRowMapperLambda.userRowMapperLambda);
} catch (Exception e) {
	e.printStackTrace();
} finally {
	if(userModel != null) {
		response.setCode(Constant.CodeSuccess);
		response.setMessage(Constant.MessageSuccess);
		response.setData(userModel);
	}
	else {
		response.setCode(Constant.CodeFailure);
		response.setMessage(Constant.MessageFailure);
	}
}
return response;
}*/
