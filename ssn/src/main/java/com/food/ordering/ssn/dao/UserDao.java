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
    ShopDao shopDao;

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
            if (userModel == null)
                return response;

            if (userModel.getOauthId() == null) {
                userModel.setOauthId(user.getOauthId());
                updateOauthId(userModel);
            }

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
            } else {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                List<ShopModel> shopModelList = new ArrayList<>();
                for (UserShopModel userShopModel : userShopModelResponse.getData()) {
                    Response<ShopModel> shopModelResponse = shopDao.getShopById(userShopModel.getShopModel().getId(), userModel.getOauthId(), userModel.getMobile());
                    if (shopModelResponse.getCode().equals(ErrorLog.CodeSuccess))
                        shopModelList.add(shopModelResponse.getData());
                }
                userShopListModel.setShopModelList(shopModelList);
                response.setData(userShopListModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**************************************************/

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

    /**************************************************/

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
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            } else
                response.setMessage(ErrorLog.UserDetailNotUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<CollegeModel> updateCollegeByMobile(UserCollegeModel userCollegeModel, String oauthId, String mobile) {
        Response<CollegeModel> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserCollegeColumn.mobile, userCollegeModel.getUserModel().getMobile())
                    .addValue(UserCollegeColumn.collegeId, userCollegeModel.getCollegeModel().getId());

            int result = namedParameterJdbcTemplate.update(UserCollegeQuery.updateCollegeByMobile, parameters);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                return collegeDao.getCollegeById(userCollegeModel.getCollegeModel().getId(), oauthId, mobile);
            } else {
                result = namedParameterJdbcTemplate.update(UserCollegeQuery.insertUserCollege, parameters);
                if (result > 0) {
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    return collegeDao.getCollegeById(userCollegeModel.getCollegeModel().getId(), oauthId, mobile);
                } else
                    response.setMessage(ErrorLog.CollegeDetailNotAvailable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<String> updateOauthId(UserModel user) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            int result = namedParameterJdbcTemplate.update(UserQuery.updateOauthId, parameters);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            } else
                response.setMessage(ErrorLog.UserDetailNotUpdated);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response<String> updateUserCollegeData(UserCollegeModel userCollegeModel, String oauthId, String mobile) {
        Response<String> response = new Response<>();
        Response<String> responseUser = updateUser(userCollegeModel.getUserModel(), oauthId, mobile);
        Response<CollegeModel> responseCollege = updateCollegeByMobile(userCollegeModel, oauthId, mobile);
        if(responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)){
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        }
        else if(!responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)){
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.CollegeDetailNotUpdated);
        }
        else if(responseUser.getCode().equals(ErrorLog.CodeSuccess) && !responseCollege.getCode().equals(ErrorLog.CodeSuccess)){
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.UserDetailNotUpdated);
        }
        return response;
    }

    /**************************************************/

    public Response<String> deleteUserByOauthId(String oauthId, String oauthIdRh, String mobile) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthIdRh, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.oauthId, oauthId);
            namedParameterJdbcTemplate.update(UserQuery.deleteUser, parameters);
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
