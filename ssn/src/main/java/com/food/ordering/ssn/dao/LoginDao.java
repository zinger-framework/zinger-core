package com.food.ordering.ssn.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.food.ordering.ssn.model.CollegeModel;
import com.food.ordering.ssn.model.ShopModel;
import com.food.ordering.ssn.model.UserCollege;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.model.UserShop;
import com.food.ordering.ssn.query.CollegeQuery;
import com.food.ordering.ssn.query.LoginQuery;
import com.food.ordering.ssn.query.ShopQuery;
import com.food.ordering.ssn.query.UserShopQuery;
import com.food.ordering.ssn.query.UsersCollegeQuery;
import com.food.ordering.ssn.rowMapperLambda.CollegeRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.LoginRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.ShopRowMapperLambda;
import com.food.ordering.ssn.utils.Constant;
import com.food.ordering.ssn.utils.Enums.UserRole;
import com.food.ordering.ssn.utils.Response;

@Repository
public class LoginDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	UtilsDao utilsDao;

	@Autowired
	CollegeDao collegeDao;

	public Response<?> insertUser(UserModel user) {
		Response<?> resp = new Response<>();

		try {
			
			Response<UserModel> validUser = utilsDao.validateUser(user.getOauthId());
			if(validUser.getCode() == Constant.CodeSuccess) {
				Response<UserModel> validateResp = new Response<>();
				validateResp.setCode(Constant.CodeFailure);
				validateResp.setMessage("User already exists");
				validateResp.setData(validUser.getData());
				return validateResp;
			}
			
			

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", user.getOauthId())
					.addValue("name", user.getName()).addValue("email", user.getEmail())
					.addValue("mobile", user.getMobile()).addValue("role", user.getRole());

			namedParameterJdbcTemplate.update(LoginQuery.insertUser, parameters);
			user.setIsDelete(0);

			if (user.getRole().equalsIgnoreCase(UserRole.customer.value())) {
				List<CollegeModel> colleges = namedParameterJdbcTemplate.query(CollegeQuery.getAllColleges,
						CollegeRowMapperLambda.collegeRowMapperLambda);

				UserCollege userCollege = new UserCollege(user, colleges);

				Response<UserCollege> response = new Response<>();
				response.setData(userCollege);
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				return response;
			} else if (user.getRole().equalsIgnoreCase(UserRole.seller.value())) {

				List<ShopModel> shops = namedParameterJdbcTemplate.query(ShopQuery.getAllShops,
						ShopRowMapperLambda.shopRowMapperLambda);

				UserShop userShop = new UserShop(user, shops);

				Response<UserShop> response = new Response<>();
				response.setData(userShop);
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}

	public Response<List<UserModel>> getAllUser(String oauthId) {
		Response<List<UserModel>> response = new Response<>();
		List<UserModel> list = null;

		try {
			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			list = jdbcTemplate.query(LoginQuery.getAllUser, LoginRowMapperLambda.userRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (list != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(list);
			}
		}
		return response;
	}

	public Response<UserModel> getUserByOauthId(String oauthId, String oauthIdRh) {
		UserModel userModel = null;
		Response<UserModel> response = new Response<>();

		try {
			if (utilsDao.validateUser(oauthIdRh).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", oauthId);
			userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserByOauthId, parameters,
					LoginRowMapperLambda.userRowMapperLambda);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (userModel != null) {
				response.setCode(Constant.CodeSuccess);
				response.setMessage(Constant.MessageSuccess);
				response.setData(userModel);
			}
		}
		return response;
	}

	public Response<String> updateUserByOauthId(UserModel user, String oauthId, Integer id) {
		Response<String> response = new Response<>();
		boolean userUpdated = false;
		boolean roleUpdated = false;
		String tableNotUpdated = "";
		try {
			if (utilsDao.validateUser(oauthId).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", user.getName())
					.addValue("email", user.getEmail()).addValue("mobile", user.getMobile())
					.addValue("oauth_id", oauthId);

			namedParameterJdbcTemplate.update(LoginQuery.updateUserByOauthId, parameters);
			userUpdated = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			if (UserRole.customer.value().equalsIgnoreCase(user.getRole())) {
				SqlParameterSource params = new MapSqlParameterSource().addValue("oauth_id", oauthId)
						.addValue("college_id", id);

				tableNotUpdated = "Users_College not updated";
				namedParameterJdbcTemplate.update(UsersCollegeQuery.insertObject, params);
				roleUpdated = true;
			} else if (UserRole.seller.value().equalsIgnoreCase(user.getRole())) {
				SqlParameterSource params = new MapSqlParameterSource().addValue("oauth_id", oauthId)
						.addValue("shop_id", id);
				
				tableNotUpdated = "Users_Shop not updated";
				namedParameterJdbcTemplate.update(UserShopQuery.insertObject, params);
				roleUpdated = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!userUpdated && !roleUpdated) {
			response.setData(Constant.TablesNotUpdated);
		}else if(userUpdated && !roleUpdated) {
			response.setCode(Constant.CodeFailure);
			response.setData(tableNotUpdated);
		}else if(userUpdated && roleUpdated) {
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
			response.setData(Constant.MessageSuccess);
		}

		return response;
	}

	public Response<UserModel> deleteUserByOauthId(String oauthId, String oauthIdRh) {
		Response<UserModel> response = new Response<>();

		try {
			if (utilsDao.validateUser(oauthIdRh).getCode() != Constant.CodeSuccess)
				return response;

			SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", oauthId);
			namedParameterJdbcTemplate.update(LoginQuery.deleteUserByOauthId, parameters);

			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.MessageSuccess);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}

/*
 * public Response<UserModel> getUserById(Integer id) { UserModel userModel =
 * null; Response<UserModel> response = new Response<>();
 * 
 * try { SqlParameterSource parameters = new
 * MapSqlParameterSource().addValue("id", id); userModel =
 * namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserById, parameters,
 * LoginRowMapperLambda.userRowMapperLambda); } catch (Exception e) {
 * e.printStackTrace(); } finally { if(userModel != null) {
 * response.setCode(Constant.CodeSuccess);
 * response.setMessage(Constant.MessageSuccess); response.setData(userModel); }
 * else { response.setCode(Constant.CodeFailure);
 * response.setMessage(Constant.MessageFailure); } } return response; }
 */
