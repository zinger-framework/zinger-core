package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column;
import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.constant.Column.UserPlaceColumn;
import com.food.ordering.zinger.constant.Column.UserShopColumn;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query;
import com.food.ordering.zinger.constant.Query.UserPlaceQuery;
import com.food.ordering.zinger.constant.Query.UserQuery;
import com.food.ordering.zinger.constant.Query.UserShopQuery;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.UserLogModel;
import com.food.ordering.zinger.rowMapperLambda.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

@Repository
public class UserDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    PlaceDao placeDao;

    @Autowired
    ShopDao shopDao;

    @Autowired
    ConfigurationDao configurationDao;

    @Autowired
    RatingDao ratingDao;

    @Autowired
    NotifyDao notifyDao;

    @Autowired
    InterceptorDao interceptorDao;

    @Autowired
    AuditLogDao auditLogDao;

    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        Response<UserPlaceModel> response = new Response<>();
        Priority priority = Priority.HIGH;
        UserPlaceModel userPlaceModel = new UserPlaceModel();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            UserModel userModel = null;
            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobileOauth, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            if (userModel != null) {
                userPlaceModel.setUserModel(userModel);
                response = getPlaceByUserId(userModel);
                priority = Priority.LOW;
                response.setCode(ErrorLog.CodeSuccess);
            } else {
                Number responseValue = insertUser(user);
                if (responseValue != null && responseValue.intValue() > 0) {
                    priority = Priority.LOW;
                    response.setCode(ErrorLog.PDNA1163);
                    response.setMessage(ErrorLog.PlaceDetailNotAvailable);
                    user.setId(responseValue.intValue());
                    user.setRole(UserRole.CUSTOMER);
                    userPlaceModel.setUserModel(user);
                    response.setData(userPlaceModel);
                } else {
                    response.setCode(ErrorLog.UDNU1151);
                }
            }

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1152);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, null, null, user.toString(), priority));
        return response;
    }

    private Number insertUser(UserModel userModel){
        if(userModel.getRole() == null)
            userModel.setRole(UserRole.CUSTOMER);

        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, userModel.getMobile())
                    .addValue(UserColumn.oauthId, userModel.getOauthId())
                    .addValue(UserColumn.role, userModel.getRole().name())
                    .addValue(UserColumn.isDelete, 0);

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
            simpleJdbcInsert.withTableName(UserColumn.tableName).usingGeneratedKeyColumns(UserColumn.id);
            return simpleJdbcInsert.executeAndReturnKey(parameters);
        }
        catch (Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    public Response<UserShopListModel> verifySeller(UserModel user) {
        Response<UserShopListModel> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            UserModel userModel = null;
            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobileOauth, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            if (userModel != null && !userModel.getRole().equals(UserRole.CUSTOMER.name())) {
                response = getShopByUserId(userModel);
                priority = Priority.LOW;
            } else
                response.setCode(ErrorLog.UDNU1155);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            response.setCode(ErrorLog.CE1154);
        }

        auditLogDao.insertUserLog(new UserLogModel(response, null, null, user.toString(), priority));
        return response;
    }

    public Response<UserInviteModel> verifyInvite(Integer shopId, String mobile) {
        Response<UserInviteModel> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        UserInviteModel userInviteModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(Column.UserInviteColumn.shopId, shopId)
                    .addValue(Column.UserInviteColumn.mobile, mobile);

            try {
                userInviteModel = namedParameterJdbcTemplate.queryForObject(Query.UserInviteQuery.verifyInvite, parameters, UserInviteRowMapperLambda.sellerInviteModelRowMapper);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                if (userInviteModel != null) {
                    response.setCode(CodeSuccess);
                    response.setMessage(Success);
                    userInviteModel.getShopModel().setPlaceModel(null);
                    response.setData(userInviteModel);
                } else {
                    response.setCode(ErrorLog.IE1166);
                    response.setMessage(ErrorLog.InviteExpired);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1108);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, null, null, shopId.toString(), priority));
        return response;
    }

    public Response<String> inviteSeller(UserShopModel userShopModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.IH1027);
                    response.setData(ErrorLog.InvalidHeader);
                } else {
                    SqlParameterSource parameters = new MapSqlParameterSource()
                            .addValue(Column.UserInviteColumn.mobile, userShopModel.getUserModel().getMobile())
                            .addValue(Column.UserInviteColumn.role, userShopModel.getUserModel().getRole().name())
                            .addValue(Column.UserInviteColumn.shopId, userShopModel.getShopModel().getId());

                    int responseValue = namedParameterJdbcTemplate.update(Query.UserInviteQuery.inviteSeller, parameters);
                    if (responseValue > 0) {
                        notifyDao.notifyInvitation(userShopModel);
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        priority = Priority.LOW;
                    } else {
                        response.setCode(ErrorLog.UDNU1165);
                        response.setMessage(UserDetailNotUpdated);
                    }
                }
            } else {
                priority = Priority.HIGH;
                response.setCode(ErrorLog.IH1061);
                response.setData(ErrorLog.InvalidHeader);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1107);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), null, userShopModel.toString(), priority));
        return response;
    }

    public Response<String> acceptInvite(UserShopModel userShopModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;
        Response<UserInviteModel> inviteModelResponse = verifyInvite(userShopModel.getShopModel().getId(), userShopModel.getUserModel().getMobile());

        try {
            if (inviteModelResponse.getCode().equals(CodeSuccess)) {
                userShopModel.getUserModel().setRole(inviteModelResponse.getData().getUserModel().getRole());
                Number responseValue = insertUser(userShopModel.getUserModel());
                if (responseValue != null && responseValue.intValue() > 0) {
                    userShopModel.getUserModel().setId(responseValue.intValue());
                    response = updateShop(userShopModel);
                    priority = Priority.LOW;
                } else {
                    Response<UserModel> userModelResponse = getUserByMobile(userShopModel.getUserModel().getMobile());
                    if(userModelResponse != null) {
                        Response<String> updateRoleResponse = updateRole(userModelResponse.getData().getId(), inviteModelResponse.getData().getUserModel().getRole());
                        if (updateRoleResponse.getCode().equals(CodeSuccess)) {
                            response = updateShop(userShopModel);
                            priority = Priority.LOW;
                        } else {
                            response.setCode(ErrorLog.UDNU1153);
                            response.setMessage(UserDetailNotUpdated);
                        }
                    }
                    else{
                        response.setCode(ErrorLog.UDNA1262);
                        response.setMessage(UserDetailNotAvailable);
                    }
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1213);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, userShopModel.getUserModel().getId(), null, userShopModel.getUserModel().getMobile(), priority));
        return response;
    }

    /**************************************************/

    public Response<UserPlaceModel> getPlaceByUserId(UserModel userModel) {
        Response<UserPlaceModel> response = new Response<>();
        UserPlaceModel userPlaceModel = null;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserPlaceColumn.userId, userModel.getId());

        try {
            userPlaceModel = namedParameterJdbcTemplate.queryForObject(UserPlaceQuery.getPlaceByUserId, parameters, UserPlaceRowMapperLambda.userPlaceRowMapperLambda);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (userPlaceModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);

                if (userPlaceModel.getPlaceModel().getName() == null || userPlaceModel.getPlaceModel().getName().isEmpty()) {
                    Response<PlaceModel> placeModelResponse = placeDao.getPlaceById(userPlaceModel.getPlaceModel().getId());
                    userPlaceModel.setPlaceModel(placeModelResponse.getData());
                }
                userPlaceModel.setUserModel(userModel);
                response.setData(userPlaceModel);
            } else
                response.setMessage(ErrorLog.PlaceDetailNotAvailable);
        }

        return response;
    }

    public Response<UserModel> getUserById(Integer id) {
        Response<UserModel> response = new Response<>();
        UserModel userModel = null;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserColumn.id, id);

        try {
            userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.getUserById, parameters, UserRowMapperLambda.userRowMapperLambda);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (userModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userModel);
            }
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (userModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userModel);
            }
        }

        return response;
    }

    public Response<UserShopListModel> getShopByUserId(UserModel userModel) {
        Response<UserShopListModel> response = new Response<>();
        UserShopListModel userShopListModel = null;
        List<ShopConfigurationModel> shopConfigurationModelList = null;
        List<UserShopModel> userShopModelList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.userId, userModel.getId());

            try {
                userShopModelList = namedParameterJdbcTemplate.query(UserShopQuery.getShopByUserId, parameters, UserShopRowMapperLambda.userShopRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (userShopModelList != null && !userShopModelList.isEmpty()) {
                userShopListModel = new UserShopListModel();
                shopConfigurationModelList = new ArrayList<>();

                for (int i = 0; i < userShopModelList.size(); i++) {
                    ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();

                    Response<ShopModel> shopModelResponse = shopDao.getShopById(userShopModelList.get(i).getShopModel().getId());
                    shopConfigurationModel.setShopModel(shopModelResponse.getData());

                    Response<RatingModel> ratingModelResponse = ratingDao.getRatingByShopId(shopModelResponse.getData());
                    ratingModelResponse.getData().setShopModel(null);
                    shopConfigurationModel.setRatingModel(ratingModelResponse.getData());

                    Response<ConfigurationModel> configurationModelResponse = configurationDao.getConfigurationByShopId(shopModelResponse.getData());
                    configurationModelResponse.getData().setShopModel(null);
                    shopConfigurationModel.setConfigurationModel(configurationModelResponse.getData());

                    shopConfigurationModelList.add(shopConfigurationModel);
                }
                userShopListModel.setUserModel(userModel);
                userShopListModel.setShopModelList(shopConfigurationModelList);

                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userShopListModel);
            } else {
                response.setCode(ErrorLog.SDNA1168);
                response.setMessage(ErrorLog.ShopDetailNotAvailable);
            }
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
            } else if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
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
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                } finally {
                    if (userModelList != null && !userModelList.isEmpty()) {
                        priority = Priority.LOW;
                        userModelResponse.setCode(ErrorLog.CodeSuccess);
                        userModelResponse.setMessage(ErrorLog.Success);
                        userModelResponse.setData(userModelList);
                    } else
                        userModelResponse.setCode(ErrorLog.CE1104);
                }
            }
        } catch (Exception e) {
            userModelResponse.setCode(ErrorLog.CE1105);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(userModelResponse, requestHeaderModel.getId(), null, shopId.toString(), priority));
        return userModelResponse;
    }

    /**************************************************/

    public Response<String> updateUser(UserModel user, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1051);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.InvalidHeader);
                priority = Priority.HIGH;
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserColumn.name, user.getName())
                        .addValue(UserColumn.mobile, user.getMobile())
                        .addValue(UserColumn.email, user.getEmail())
                        .addValue(UserColumn.id, user.getId());

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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), user.getId(), user.toString(), priority));
        return response;
    }

    public Response<String> updateRole(Integer id, UserRole role) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.id, id)
                    .addValue(UserColumn.role, role.name());

            int result = namedParameterJdbcTemplate.update(UserQuery.updateRole, parameters);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    public Response<String> updatePlace(UserPlaceModel userPlaceModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserPlaceColumn.userId, userPlaceModel.getUserModel().getId())
                    .addValue(UserPlaceColumn.placeId, userPlaceModel.getPlaceModel().getId());

            int result = namedParameterJdbcTemplate.update(UserPlaceQuery.updatePlaceByMobile, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserPlaceQuery.insertUserPlace, parameters);

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    public Response<String> updateShop(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.userId, userShopModel.getUserModel().getId())
                    .addValue(UserShopColumn.shopId, userShopModel.getShopModel().getId());

            int result = namedParameterJdbcTemplate.update(UserShopQuery.updateShopById, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserShopQuery.insertUserShop, parameters);

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.IH1052);
            response.setMessage(ErrorLog.InvalidHeader);
            priority = Priority.HIGH;
        } else {
            Response<String> responseUser = updateUser(userPlaceModel.getUserModel(), requestHeaderModel);
            Response<String> responsePlace = updatePlace(userPlaceModel);

            if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && responsePlace.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            } else if (!responseUser.getCode().equals(ErrorLog.CodeSuccess) && responsePlace.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.UDNU1159);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.UserDetailNotUpdated);
            } else if (responseUser.getCode().equals(ErrorLog.CodeSuccess) && !responsePlace.getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.CDNU1160);
                response.setMessage(ErrorLog.Failure);
                response.setData(ErrorLog.PlaceDetailNotUpdated);
            }
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), userPlaceModel.getUserModel().getId(), userPlaceModel.toString(), priority));
        return response;
    }

    public Response<String> deleteSeller(Integer shopId, Integer userId, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.HIGH;

        try {
            if (!requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                response.setCode(ErrorLog.IH1025);
                response.setData(ErrorLog.InvalidHeader);
            } else if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1026);
                response.setData(ErrorLog.InvalidHeader);
            } else {
                SqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue(UserShopColumn.userId, userId)
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
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), userId, null, priority));
        return response;
    }

    public Response<String> deleteInvite(UserShopModel userShopModel, RequestHeaderModel requestHeaderModel) {
        Response<String> response = new Response<>();
        Priority priority = Priority.MEDIUM;

        try {
            if (requestHeaderModel.getRole().equals(UserRole.SHOP_OWNER.name())) {
                if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                    response.setCode(ErrorLog.IH1050);
                    response.setData(ErrorLog.InvalidHeader);
                } else {
                    SqlParameterSource parameters = new MapSqlParameterSource()
                            .addValue(Column.UserInviteColumn.mobile, userShopModel.getUserModel().getMobile())
                            .addValue(Column.UserInviteColumn.role, userShopModel.getUserModel().getRole().name())
                            .addValue(Column.UserInviteColumn.shopId, userShopModel.getShopModel().getId());

                    int responseValue = namedParameterJdbcTemplate.update(Query.UserInviteQuery.deleteInvite, parameters);
                    if (responseValue > 0) {
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        priority = Priority.LOW;
                    } else {
                        response.setCode(ErrorLog.UDND1162);
                        response.setMessage(UserDetailNotDeleted);
                    }
                }
            } else {
                priority = Priority.HIGH;
                response.setCode(ErrorLog.IH1059);
                response.setData(ErrorLog.InvalidHeader);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1161);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), null, userShopModel.toString(), priority));
        return response;
    }

}
