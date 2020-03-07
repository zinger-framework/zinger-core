package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.UserCollegeColumn;
import com.food.ordering.ssn.column.UserColumn;
import com.food.ordering.ssn.column.UserShopColumn;
import com.food.ordering.ssn.enums.UserRole;
import com.food.ordering.ssn.model.*;
import com.food.ordering.ssn.query.UserCollegeQuery;
import com.food.ordering.ssn.query.UserQuery;
import com.food.ordering.ssn.query.UserShopQuery;
import com.food.ordering.ssn.rowMapperLambda.UserCollegeRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.UserRowMapperLambda;
import com.food.ordering.ssn.rowMapperLambda.UserShopRowMapperLambda;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

        if (!user.getRole().equals(UserRole.CUSTOMER))
            return response;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserColumn.mobile, user.getMobile());

        UserModel userModel = null;
        try {
            userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userModel != null) {
            if (!userModel.getRole().equals(UserRole.CUSTOMER))
                return response;

            response.setCode(ErrorLog.CodeSuccess);
            userCollegeModel.setUserModel(userModel);

            Response<UserCollegeModel> userCollegeModelResponse = getCollegeByMobile(userModel.getMobile(), userModel.getOauthId(), userModel.getMobile());
            if (userCollegeModelResponse.getCode().equals(ErrorLog.CodeSuccess)) {
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
        } else {
            parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId())
                    .addValue(UserColumn.role, user.getRole().name());

            int result = namedParameterJdbcTemplate.update(UserQuery.insertUser, parameters);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.CollegeDetailNotAvailable);
                userCollegeModel.setUserModel(user);
                response.setData(userCollegeModel);
            }
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

        if (!utilsDao.validateUser(oauthId, mobileRh).getCode().equals(ErrorLog.CodeSuccess))
            return response;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserCollegeColumn.mobile, mobile);

        try {
            userCollegeModel = namedParameterJdbcTemplate.queryForObject(UserCollegeQuery.getCollegeByMobile, parameters, UserCollegeRowMapperLambda.userCollegeRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userCollegeModel != null) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(userCollegeModel);
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
                    .addValue(UserColumn.email, user.getEmail());

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

    public Response<String> updateCollegeByMobile(UserCollegeModel userCollegeModel, String oauthId, String mobile) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(oauthId, mobile).getCode().equals(ErrorLog.CodeSuccess))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserCollegeColumn.mobile, userCollegeModel.getUserModel().getMobile())
                    .addValue(UserCollegeColumn.collegeId, userCollegeModel.getCollegeModel().getId());

            int result = namedParameterJdbcTemplate.update(UserCollegeQuery.updateCollegeByMobile, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserCollegeQuery.insertUserCollege, parameters);

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
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
        Response<String> responseCollege = updateCollegeByMobile(userCollegeModel, oauthId, mobile);
        if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } else if (!responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.UserDetailNotUpdated);
        } else if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && !responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.CollegeDetailNotUpdated);
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
