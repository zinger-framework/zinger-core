package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.column.UserCollegeColumn;
import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.column.UserShopColumn;
import com.food.ordering.zinger.enums.Priority;
import com.food.ordering.zinger.enums.UserRole;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.UserLogModel;
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

import static com.food.ordering.zinger.utils.ErrorLog.*;

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

    @Autowired
    AuditLogDao auditLogDao;

    public Response<UserCollegeModel> loginRegisterCustomer(UserModel user) {
        Response<UserCollegeModel> response = new Response<>();
        Priority priority = Priority.HIGH;
        UserCollegeModel userCollegeModel = new UserCollegeModel();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            UserModel userModel = null;
            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobileOauth, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (userModel != null) {
                userCollegeModel.setUserModel(userModel);
                response = getCollegeByMobile(userModel);
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
            } else {
                parameters = new MapSqlParameterSource()
                        .addValue(UserColumn.mobile, user.getMobile())
                        .addValue(UserColumn.oauthId, user.getOauthId())
                        .addValue(UserColumn.role, UserRole.CUSTOMER.name());

                int result = namedParameterJdbcTemplate.update(UserQuery.insertUser, parameters);
                if (result > 0) {
                    priority = Priority.LOW;
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.CollegeDetailNotAvailable);
                    user.setRole(UserRole.CUSTOMER);
                    userCollegeModel.setUserModel(user);
                    response.setData(userCollegeModel);
                } else
                    response.setCode(ErrorLog.UDNU1151);
            }

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1152);
            e.printStackTrace();
        }

        auditLogDao.insertUserLog(new UserLogModel(response, user.getMobile(), user.getMobile(), user.toString(), priority));
        return response;
    }

    public Response<UserShopListModel> verifySeller(UserModel user) {
        Response<UserShopListModel> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!user.getRole().equals(UserRole.CUSTOMER)) {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserColumn.mobile, user.getMobile())
                        .addValue(UserColumn.role, user.getRole().name());

                UserModel userModel = null;
                try {
                    userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobileRole, parameters, UserRowMapperLambda.userRowMapperLambda);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userModel != null) {
                    if (userModel.getOauthId() == null || userModel.getOauthId().isEmpty()) {
                        userModel.setOauthId(user.getOauthId());
                        Response<String> response1 = updateOauthId(userModel);
                        if (response1.getCode().equals(ErrorLog.CodeSuccess) && response1.getMessage().equals(ErrorLog.Success)) {
                            response = getShopByMobile(userModel);
                            priority = Priority.LOW;
                        } else
                            response.setCode(ErrorLog.UDNA1156);
                    } else {
                        response = getShopByMobile(userModel);
                        priority = Priority.LOW;
                    }
                } else
                    response.setCode(ErrorLog.UDNU1155);
            } else
                response.setCode(ErrorLog.UDNU1153);

        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ErrorLog.CE1154);
        }

        auditLogDao.insertUserLog(new UserLogModel(response, user.getMobile(), user.getMobile(), user.toString(), priority));
        return response;
    }

    public Response<String> insertSeller(Integer shopId, String mobile, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;
        UserRole userRole = UserRole.SELLER;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.SUPER_ADMIN.name()))
                userRole = UserRole.SHOP_OWNER;

            if (requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name()) || requestHeaderModel.getRole().equals(UserRole.SUPER_ADMIN.name())) {
                if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.IH1060);
                    response.setData(ErrorLog.InvalidHeader);
                } else {
                    SqlParameterSource parameters = new MapSqlParameterSource()
                            .addValue(UserColumn.mobile, mobile)
                            .addValue(UserColumn.role, userRole.name());

                    int responseValue = 0;
                    try {
                        responseValue = namedParameterJdbcTemplate.update(UserQuery.insertSeller, parameters);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    UserShopModel userShopModel = new UserShopModel();
                    userShopModel.getUserModel().setMobile(mobile);
                    userShopModel.getShopModel().setId(shopId);
                    Response<String> response1 = updateShop(userShopModel);

                    if (responseValue <= 0)
                        responseValue = namedParameterJdbcTemplate.update(UserQuery.updateRole, parameters);

                    if (responseValue > 0 && response1.getCode().equals(CodeSuccess)) {
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        priority = Priority.LOW;
                    } else if (responseValue <= 0) {
                        response.setCode(ErrorLog.UDNU1162);
                        response.setData(UserDetailNotUpdated);
                    } else {
                        response.setCode(ErrorLog.SDNU1163);
                        response.setData(ShopDetailNotUpdated);
                    }
                }
            } else {
                response.setCode(ErrorLog.IH1059);
                response.setData(ErrorLog.InvalidHeader);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1161);
            e.printStackTrace();
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getMobile(), mobile, null, priority));
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
                        ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();

                        Response<ShopModel> shopModelResponse = shopDao.getShopById(shopModelList.get(i).getId());
                        shopConfigurationModel.setShopModel(shopModelResponse.getData());

                        Response<RatingModel> ratingModelResponse = ratingDao.getRatingByShopId(shopModelResponse.getData());
                        ratingModelResponse.getData().setShopModel(null);
                        shopConfigurationModel.setRatingModel(ratingModelResponse.getData());

                        Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(shopModelResponse.getData());
                        configurationModelResponse.getData().setShopModel(null);
                        shopConfigurationModel.setConfigurationModel(configurationModelResponse.getData());

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

    public Response<List<UserModel>> getSellerByShopId(Integer shopId, RequestHeaderModel requestHeaderModel) {
        Response<List<UserModel>> userModelResponse = new Response<>();
        List<UserModel> userModelList = null;
        Priority priority = Priority.MEDIUM;

        try {
            if (!requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                userModelResponse.setCode(ErrorLog.IH1024);
                userModelResponse.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                userModelResponse.setCode(ErrorLog.IH1023);
                userModelResponse.setMessage(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserShopColumn.shopId, shopId);

                try {
                    userModelList = namedParameterJdbcTemplate.query(UserQuery.getSellerByShopId, parameters, UserRowMapperLambda.userRowMapperLambda);
                } catch (Exception e) {
                    userModelResponse.setCode(ErrorLog.CE1104);
                    e.printStackTrace();
                } finally {
                    if (userModelList != null) {
                        userModelList.removeIf(userModel -> userModel.getRole() == UserRole.SHOP_OWNER);
                        if (!userModelList.isEmpty()) {
                            priority = Priority.LOW;
                            userModelResponse.setCode(ErrorLog.CodeSuccess);
                            userModelResponse.setMessage(ErrorLog.Success);
                            userModelResponse.setData(userModelList);
                        } else
                            userModelResponse.setCode(ErrorLog.CE1104);
                    }
                }
            }
        } catch (Exception e) {
            userModelResponse.setCode(ErrorLog.CE1105);
            e.printStackTrace();
        }

        auditLogDao.insertUserLog(new UserLogModel(userModelResponse, requestHeaderModel.getMobile(), null, shopId.toString(), priority));
        return userModelResponse;
    }

    /**************************************************/

    public Response<String> updateUser(UserModel user, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1051);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserColumn.name, user.getName())
                        .addValue(UserColumn.mobile, user.getMobile())
                        .addValue(UserColumn.email, user.getEmail());

                int result = namedParameterJdbcTemplate.update(UserQuery.updateUser, parameters);
                if (result > 0) {
                    priority = Priority.LOW;
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                } else {
                    response.setCode(ErrorLog.UDNU1157);
                    response.setMessage(ErrorLog.Failure);
                    response.setMessage(ErrorLog.UserDetailNotUpdated);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1158);
            e.printStackTrace();
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getMobile(), user.getMobile(), user.toString(), priority));
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

    public Response<String> updateShop(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.mobile, userShopModel.getUserModel().getMobile())
                    .addValue(UserShopColumn.shopId, userShopModel.getShopModel().getId());

            int result = namedParameterJdbcTemplate.update(UserShopQuery.updateShopByMobile, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserShopQuery.insertUserShop, parameters);

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
        Priority priority = Priority.MEDIUM;

        if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess) || !userCollegeModel.getUserModel().getRole().equals(UserRole.CUSTOMER)) {
            response.setCode(ErrorLog.IH1052);
            response.setMessage(ErrorLog.InvalidHeader);
            priority = Priority.HIGH;
        } else {
            Response<String> responseUser = updateUser(userCollegeModel.getUserModel(), requestHeaderModel);
            Response<String> responseCollege = updateCollege(userCollegeModel);

            if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            } else if (!responseUser.getCode().equals(ErrorLog.CodeSuccess) && responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.UDNU1159);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.UserDetailNotUpdated);
            } else if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && !responseCollege.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CDNU1160);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.CollegeDetailNotUpdated);
            }
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getMobile(), userCollegeModel.getUserModel().getMobile(), userCollegeModel.toString(), priority));
        return response;
    }

    public Response<String> deleteSeller(Integer shopId, String mobile, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                response.setCode(ErrorLog.IH1025);
                response.setData(ErrorLog.InvalidHeader);
            } else if (!utilsDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1026);
                response.setData(ErrorLog.InvalidHeader);
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserShopColumn.mobile, mobile)
                        .addValue(UserShopColumn.shopId, shopId);

                int result = namedParameterJdbcTemplate.update(UserShopQuery.deleteUser, parameters);
                if (result > 0) {
                    priority = Priority.LOW;
                    response.setCode(ErrorLog.CodeSuccess);
                    response.setMessage(ErrorLog.Success);
                    response.setData(ErrorLog.Success);
                } else {
                    response.setCode(ErrorLog.UDND1164);
                    response.setData(ErrorLog.UserDetailNotDeleted);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1106);
            response.setData(Failure);
            e.printStackTrace();
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getMobile(), mobile, null, priority));
        return response;
    }
}
