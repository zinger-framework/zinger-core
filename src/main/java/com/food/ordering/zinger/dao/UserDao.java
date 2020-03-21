package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.UserCollegeColumn;
import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.column.UserShopColumn;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.query.UserCollegeQuery;
import com.food.ordering.zinger.query.UserQuery;
import com.food.ordering.zinger.query.UserShopQuery;
import com.food.ordering.zinger.rowMapperLambda.UserCollegeRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.UserRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.UserShopRowMapperLambda;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    CollegeDao collegeDao;

    @Autowired
    ShopDao shopDao;

    @Autowired
    ConfigurationDao configurationDao;

    @Autowired
    RatingDao ratingDao;

    @Autowired
    UtilsDao utilsDao;

    public Response<UserCollegeModel> insertCustomer(UserModel user) {
        Response<UserCollegeModel> response = new Response<>();
        UserCollegeModel userCollegeModel = new UserCollegeModel();
        try {
            if (!user.getRole().equals(UserRole.CUSTOMER))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.role, user.getRole().name());

            UserModel userModel = null;
            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (userModel != null) {
                userCollegeModel.setUserModel(userModel);
                response = getCollegeByMobile(userModel);
                response.setCode(ErrorLog.CodeSuccess);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<UserShopListModel> insertSeller(UserModel user) {
        Response<UserShopListModel> response = new Response<>();
        try {
            if (user.getRole().equals(UserRole.CUSTOMER))
                return response;

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.role, user.getRole().name());

            UserModel userModel = null;
            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (userModel == null)
                return response;

            if (userModel.getOauthId() == null || userModel.getOauthId().isEmpty()) {
                userModel.setOauthId(user.getOauthId());
                updateOauthId(userModel);
            }

            response = getShopByMobile(userModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**************************************************/

    public Response<UserCollegeModel> getCollegeByMobile(UserModel userModel) {
        Response<UserCollegeModel> response = new Response<>();
        UserCollegeModel userCollegeModel = null;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserCollegeColumn.mobile, userModel.getMobile());

        try {
            userCollegeModel = namedParameterJdbcTemplate.queryForObject(UserCollegeQuery.getCollegeByMobile, parameters, UserCollegeRowMapperLambda.userCollegeRowMapperLambda);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (userCollegeModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                if (userCollegeModel.getCollegeModel().getName() == null || userCollegeModel.getCollegeModel().getName().isEmpty()) {
                    Response<CollegeModel> collegeModelResponse = collegeDao.getCollegeById(userCollegeModel.getCollegeModel().getId());
                    userCollegeModel.setCollegeModel(collegeModelResponse.getData());
                }
                userCollegeModel.setUserModel(userModel);
                response.setData(userCollegeModel);
            } else
                response.setMessage(ErrorLog.CollegeDetailNotAvailable);
        }

        return response;
    }

    public Response<UserModel> getUserByMobile(String mobile) {
        Response<UserModel> response = new Response<>();
        UserModel userModel = null;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserColumn.mobile, mobile);

        try {
            userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.getUserByMobile, parameters, UserRowMapperLambda.userRowMapperLambda);
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

    public Response<UserShopListModel> getShopByMobile(UserModel userModel) {
        Response<UserShopListModel> response = new Response<>();
        List<ShopModel> shopModelList = null;
        List<ShopConfigurationModel> shopConfigurationModelList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.mobile, userModel.getMobile());

            try {
                shopModelList = namedParameterJdbcTemplate.query(UserShopQuery.getShopByMobile, parameters, UserShopRowMapperLambda.userShopRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shopModelList != null && !shopModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                shopConfigurationModelList = new ArrayList<>();
                UserShopListModel userShopListModel = new UserShopListModel();
                userShopListModel.setUserModel(userModel);

                for (int i = 0; i < shopModelList.size(); i++) {
                    if (shopModelList.get(i).getName() == null || shopModelList.get(i).getName().isEmpty()) {
                        Response<ShopModel> shopModelResponse = shopDao.getShopById(shopModelList.get(i).getId());
                        Response<RatingModel> ratingModelResponse = ratingDao.getRatingByShopId(shopModelResponse.getData());
                        Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(shopModelResponse.getData());

                        ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();
                        shopConfigurationModel.setShopModel(shopModelResponse.getData());
                        shopConfigurationModel.setConfigurationModel(configurationModelResponse.getData());
                        shopConfigurationModel.setRatingModel(ratingModelResponse.getData());
                        shopConfigurationModelList.add(shopConfigurationModel);
                    }
                }
                userShopListModel.setShopModelList(shopConfigurationModelList);
                response.setData(userShopListModel);
            } else
                response.setMessage(ErrorLog.ShopDetailNotAvailable);
        }
        return response;
    }

    /**************************************************/

    public Response<String> updateUser(UserModel user, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setMessage(ErrorLog.InvalidHeader);
                return response;
            }

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

    public Response<String> updateCollege(UserCollegeModel userCollegeModel) {
        Response<String> response = new Response<>();

        try {
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

    public Response<String> updateUserCollegeData(UserCollegeModel userCollegeModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();

        if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess) || !userCollegeModel.getUserModel().getRole().equals(UserRole.CUSTOMER)) {
            response.setMessage(ErrorLog.InvalidHeader);
            return response;
        }

        Response<String> responseUser = updateUser(userCollegeModel.getUserModel(), requestHeaderModel);
        Response<String> responseCollege = updateCollege(userCollegeModel);

        if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
        } else if (!responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.UserDetailNotUpdated);
        } else if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && !responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.CollegeDetailNotUpdated);
        }
        return response;
    }
}
