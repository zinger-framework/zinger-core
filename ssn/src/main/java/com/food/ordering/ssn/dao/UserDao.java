package com.food.ordering.ssn.dao;

import java.util.ArrayList;
import java.util.List;

import com.food.ordering.ssn.column.*;
import com.food.ordering.ssn.enums.*;
import com.food.ordering.ssn.query.*;
import com.food.ordering.ssn.rowMapperLambda.*;
import com.food.ordering.ssn.model.*;
import com.food.ordering.ssn.utils.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    @Autowired
    CollegeDao collegeDao;

    public Response<UserCollegeModel> insertCustomer(UserModel user) {
        Response<UserCollegeModel> response = new Response<>();
        UserCollegeModel userCollegeModel = new UserCollegeModel();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile());

            UserModel userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);

            if (userModel != null) {
                userCollegeModel.setUserModel(userModel);

                if (!userModel.getRole().equals(UserRole.CUSTOMER))
                    return response;

                Response<UserCollegeModel> userCollegeModelResponse = getCollegeByMobile(user.getMobile(), userModel.getOauthId(), userModel.getMobile());
                if (userCollegeModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.CodeSuccess);

                    Integer id = userCollegeModelResponse.getData().getCollegeModel().getId();
                    Response<CollegeModel> collegeModel = collegeDao.getCollegeById(id, userModel.getOauthId(), userModel.getMobile());
                    if (collegeModel.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setMessage(ErrorLog.Success);
                        userCollegeModel.setCollegeModel(collegeModel.getData());
                    } else
                        response.setMessage(ErrorLog.CollegeDetailNotAvailable);
                } else
                    response.setMessage(ErrorLog.CollegeDetailNotAvailable);

                response.setData(userCollegeModel);
                return response;
            } else {
                parameters = new MapSqlParameterSource()
                        .addValue(UserColumn.mobile, user.getMobile())
                        .addValue(UserColumn.oauthId, user.getOauthId())
                        .addValue(UserColumn.role, user.getRole());

                int result = namedParameterJdbcTemplate.update(UserQuery.insertUser, parameters);
                if (result > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.CollegeDetailNotAvailable);
                    userCollegeModel.setUserModel(user);
                    response.setData(userCollegeModel);
                }
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<UserShopListModel> insertSeller(UserModel user) {
        Response<UserShopListModel> response = new Response<>();
        UserShopListModel userShopListModel = new UserShopListModel();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile());

            UserModel userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);
            if(userModel == null)
                return response;

            userShopListModel.setUserModel(userModel);
            if (userModel.getRole().equals(UserRole.CUSTOMER)) {
                response.setData(userShopListModel);
                return response;
            }

            Response<List<UserShopModel>> userShopModelResponse = getShopByMobile(user.getMobile(), userModel.getOauthId(), userModel.getMobile());
            if (!userShopModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.ShopDetailNotAvailable);
                response.setData(userShopListModel);
                return response;
            }
            else {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                List<ShopModel> shopModelList = new ArrayList<>();
                for (UserShopModel userShopModel : userShopModelResponse.getData()) {
                    parameters = new MapSqlParameterSource()
                            .addValue(ShopColumn.id, userShopModel.getShopModel().getId());

                    ShopModel shopModel = namedParameterJdbcTemplate.queryForObject(ShopQuery.getShopById, parameters, ShopRowMapperLambda.shopRowMapperLambda);
                    if (shopModel != null)
                        shopModelList.add(shopModel);
                }
                response.setData(userShopListModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Response<CollegeModel> getUserCollegeDetail(UserModel user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("oauth_id", user.getOauthId());

        CollegeModel college = namedParameterJdbcTemplate.queryForObject(CollegeQuery.getCollegeByOauthId, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);

        UserCollegeModel userCollegeModel = new UserCollegeModel(user, college);

        Response<UserCollegeModel> response = new Response<>();
        response.setData(userCollegeModel);
        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        return response;
    }

	/*private Response<UserCollege> getUserCollegeDetail(UserModel user) {
		if (UserRole.seller.value().equalsIgnoreCase(user.getRole())) {
			List<ShopModel> shops = namedParameterJdbcTemplate.query(ShopQuery.getAllShops, ShopRowMapperLambda.shopRowMapperLambda);

			UserShop userShop = new UserShop(user, shops);

			Response<UserShop> response = new Response<>();
			response.setData(userShop);
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.Success);
			return response;
		} else {
			SqlParameterSource parameters = new MapSqlParameterSource()
					.addValue("oauth_id", user.getOauthId());

			CollegeModel college = namedParameterJdbcTemplate.queryForObject(CollegeQuery.getCollegeById, parameters, CollegeRowMapperLambda.collegeRowMapperLambda);

			UserCollege userCollege = new UserCollege(user, college);

			Response<UserCollege> response = new Response<>();
			response.setData(userCollege);
			response.setCode(Constant.CodeSuccess);
			response.setMessage(Constant.Success);
			return response;
		}
	}*/

    public Response<List<UserModel>> getAllUser(String oauthId) {
        Response<List<UserModel>> response = new Response<>();
        List<UserModel> list = null;

        try {
            if (utilsDao.validateUser(oauthId).getCode() != ErrorLog.CodeSuccess)
                return response;

            list = jdbcTemplate.query(LoginQuery.getAllUser, UserRowMapperLambda.userRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(list);
            }
        }
        return response;
    }

    public Response<UserModel> getUserByOauthId(String oauthId, String oauthIdRh) {
        UserModel userModel = null;
        Response<UserModel> response = new Response<>();

        try {
            if (utilsDao.validateUser(oauthIdRh).getCode() != ErrorLog.CodeSuccess)
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", oauthId);
            userModel = namedParameterJdbcTemplate.queryForObject(LoginQuery.getUserByOauthId, parameters,
                    UserRowMapperLambda.userRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (userModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userModel);
            }
        }
        return response;
    }

    public Response<UserCollegeModel> getCollegeByMobile(String mobile, String oauthId, String mobileRh) {
        Response<UserCollegeModel> response = new Response<>();
        UserCollegeModel userCollegeModel = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobileRh).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserCollegeColumn.mobile, mobile);
            userCollegeModel = namedParameterJdbcTemplate.queryForObject(UserCollegeQuery.getCollegeByMobile, parameters, UserCollegeRowMapperLambda.userCollegeRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (userCollegeModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userCollegeModel);
            }
        }
        return response;
    }

    public Response<List<UserShopModel>> getShopByMobile(String mobile, String oauthId, String mobileRh) {
        Response<List<UserShopModel>> response = new Response<>();
        List<UserShopModel> userShopModelList = null;

        try {
            if (!utilsDao.validateUser(oauthId, mobileRh).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.mobile, mobile);

            userShopModelList = namedParameterJdbcTemplate.query(UserShopQuery.getShopByMobile, parameters, UserShopRowMapperLambda.userShopRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (userShopModelList != null && userShopModelList.size() > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userShopModelList);
            }
        }
        return response;
    }

    public Response<String> updateUser(UserModel user, String oauthId, String mobile) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.name, user.getName())
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.email, user.getEmail())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            int result = namedParameterJdbcTemplate.update(UserQuery.updateUser, parameters);
            if(result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
            else
                response.setMessage(ErrorLog.UserDetailNotUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (UserRole.customer.value().equalsIgnoreCase(user.getRole())) {
                SqlParameterSource params = new MapSqlParameterSource().addValue("oauth_id", user.getOauthId())
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

        if (!userUpdated && !roleUpdated) {
            response.setData(ErrorLog.TablesNotUpdated);
        } else if (userUpdated && !roleUpdated) {
            response.setCode(ErrorLog.CodeFailure);
            response.setData(tableNotUpdated);
        } else if (userUpdated && roleUpdated) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
        }

        return response;
    }

    public Response<String> updateUserCollege(UserModel user, CollegeModel collegeModel, String oauthId, String mobile) {
        Response<String> response = updateUser(user, oauthId, mobile);

        return response;
    }

    public Response<UserModel> deleteUserByOauthId(String oauthId, String oauthIdRh) {
        Response<UserModel> response = new Response<>();

        try {
            if (utilsDao.validateUser(oauthIdRh).getCode() != ErrorLog.CodeSuccess)
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource().addValue("oauth_id", oauthId);
            namedParameterJdbcTemplate.update(LoginQuery.deleteUserByOauthId, parameters);

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
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
 * response.setMessage(Constant.Success); response.setData(userModel); }
 * else { response.setCode(Constant.CodeFailure);
 * response.setMessage(Constant.Failure); } } return response; }
 */
