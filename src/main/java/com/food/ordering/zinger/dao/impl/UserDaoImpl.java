package com.food.ordering.zinger.dao.impl;

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
import com.food.ordering.zinger.dao.interfaces.UserDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.rowMapperLambda.UserRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;
import static com.food.ordering.zinger.constant.Sql.DOUBLE_QUOTE;

/**
 * UserDao is responsible for CRUD operations in
 * Users, UsersPlace, UsersShop table in MySQL.
 *
 * @implNote Request Header (RH) parameter is sent in all endpoints
 * to avoid unauthorized access to our service.
 * @implNote Authentication & Invitation Apis alone won't have RH parameter.
 * @implNote All endpoint services are audited for both success and error responses
 * using "AuditLogDao".
 * <p>
 * Endpoints starting with "/user" invoked here.
 */
@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    NotifyDaoImpl notifyDaoImpl;

    /**
     * Customer Authentication
     * Handles both Login/Register process.
     *
     * @param user UserModel
     * @return whether the user credentials exist, along with the
     * details of the user and place he(she) belongs.
     * @implNote If the user credentials doesn't exist, then registration process is executed.
     */
    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        Response<UserPlaceModel> response = new Response<>();
        response.prioritySet(Priority.HIGH);
        UserPlaceModel userPlaceModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            try {
                userPlaceModel = namedParameterJdbcTemplate.queryForObject(UserQuery.customerLogin, parameters, UserRowMapperLambda.userPlaceRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            if (userPlaceModel != null) {
                response.setCode(CodeSuccess);
                response.setMessage(Success);
                response.setData(userPlaceModel);
            } else {
                UserModel userModel = null;
                try {
                    userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.loginUserByMobileOauth, parameters, UserRowMapperLambda.userLoginRowMapperLambda);
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }

                if (userModel != null) {
                    userPlaceModel = new UserPlaceModel();
                    userPlaceModel.setUserModel(userModel);

                    response.prioritySet(Priority.LOW);
                    response.setCode(ErrorLog.PDNA1163);
                    response.setMessage(ErrorLog.PlaceDetailNotAvailable);
                    response.setData(userPlaceModel);
                } else {
                    Number responseValue = insertUser(user);
                    if (responseValue != null && responseValue.intValue() > 0) {
                        user.setId(responseValue.intValue());
                        user.setMobile(null);
                        user.setOauthId(null);
                        user.setNotificationToken(null);
                        userPlaceModel = new UserPlaceModel();
                        userPlaceModel.setUserModel(user);

                        response.prioritySet(Priority.LOW);
                        response.setCode(ErrorLog.PDNA1163);
                        response.setMessage(ErrorLog.PlaceDetailNotAvailable);
                        response.setData(userPlaceModel);
                    } else {
                        response.setCode(ErrorLog.UDNU1151);
                        response.setMessage(ErrorLog.UserHasBeenBlocked);
                    }
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1152);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Inserts the User details in the database.
     *
     * @param userModel UserModel
     * @return User id, the last generated(auto-incremented) id in the Users table.
     */
    private Number insertUser(UserModel userModel) {
        if (userModel.getRole() == null)
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
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Seller Authentication
     * Handles only Login and NOT Register process
     *
     * @param user UserModel
     * @return whether the user credentials matches with our database,
     * along with the details of the seller and shop he(she) works.
     */
    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        Response<UserShopListModel> response = new Response<>();
        response.prioritySet(Priority.HIGH);
        List<SellerLoginResponse> sellerLoginResponseList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.mobile, user.getMobile())
                    .addValue(UserColumn.oauthId, user.getOauthId());

            try {
                sellerLoginResponseList = namedParameterJdbcTemplate.query(UserQuery.sellerLogin, parameters, UserRowMapperLambda.userShopDetailRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            if (sellerLoginResponseList != null && !sellerLoginResponseList.isEmpty()) {
                UserShopListModel userShopListModel = new UserShopListModel();
                userShopListModel.setUserModel(sellerLoginResponseList.get(0).getUserModel());

                List<ShopConfigurationModel> shopConfigurationModelList = new ArrayList<>();
                for (SellerLoginResponse sellerLoginResponse : sellerLoginResponseList) {
                    ShopConfigurationModel shopConfigurationModel = new ShopConfigurationModel();
                    shopConfigurationModel.setShopModel(sellerLoginResponse.getShopModel());
                    shopConfigurationModel.setConfigurationModel(sellerLoginResponse.getConfigurationModel());
                    shopConfigurationModel.setRatingModel(sellerLoginResponse.getRatingModel());
                    shopConfigurationModelList.add(shopConfigurationModel);
                }
                userShopListModel.setShopModelList(shopConfigurationModelList);

                response.prioritySet(Priority.LOW);
                response.setCode(CodeSuccess);
                response.setMessage(Success);
                response.setData(userShopListModel);

            } else {
                response.setCode(ErrorLog.UDNA1155);
                response.setMessage(UserDetailNotAvailable);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            response.setCode(ErrorLog.CE1154);
        }

        return response;
    }

    /**
     * Invite new Seller, ShopOwner, Delivery Boy, etc to the shop.
     * Authorized by SHOP_OWNER only.
     *
     * @param userShopModel UserShopModel
     * @return success response if the invite is sent successfully.
     * @implNote Invitation is sent through SMS to the new user in the below format:
     * http://domain-name.com/user/verify/invite/{shopId}/{newUserMobileNumber}
     * Sample SMS URL: http://domain-name.com/user/verify/invite/1/9176712345
     */
    @Override
    public Response<String> inviteSeller(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(Column.UserInviteColumn.mobile, userShopModel.getUserModel().getMobile())
                    .addValue(Column.UserInviteColumn.role, userShopModel.getUserModel().getRole().name())
                    .addValue(Column.UserInviteColumn.shopId, userShopModel.getShopModel().getId());

            int responseValue = namedParameterJdbcTemplate.update(Query.UserInviteQuery.inviteSeller, parameters);
            if (responseValue > 0) {
                notifyDaoImpl.notifyInvitation(userShopModel);
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(ErrorLog.UDNU1165);
                response.setMessage(UserDetailNotUpdated);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1107);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Verify the invited user with the help of URL.
     * Invite URL Expiration Time: 15 minutes
     *
     * @param shopId Integer
     * @param mobile String
     * @return success response unless the invite is invalid or expired.
     * @implNote Sample SMS URL: http://domain-name.com/user/verify/invite/1/9176712345
     */
    @Override
    public Response<UserModel> verifyInvite(Integer shopId, String mobile) {
        Response<UserModel> response = new Response<>();
        UserModel userModel = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(Column.UserInviteColumn.shopId, shopId)
                    .addValue(Column.UserInviteColumn.mobile, mobile);

            try {
                userModel = namedParameterJdbcTemplate.queryForObject(Query.UserInviteQuery.verifyInvite, parameters, UserRowMapperLambda.userRoleRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                if (userModel != null) {
                    response.setCode(CodeSuccess);
                    response.setMessage(Success);
                    response.prioritySet(Priority.LOW);
                    response.setData(userModel);
                } else {
                    response.setCode(ErrorLog.IE1166);
                    response.setMessage(ErrorLog.InviteExpired);
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1108);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Add the invited user to the shop.
     *
     * @param userShopModel UserShopModel
     * @return success response if the mobile number verification is completed successfully.
     */
    @Override
    public Response<UserShopListModel> acceptInvite(UserShopModel userShopModel) {
        Response<UserShopListModel> response = new Response<>();
        Response<UserModel> inviteModelResponse = verifyInvite(userShopModel.getShopModel().getId(), userShopModel.getUserModel().getMobile());

        try {
            if (inviteModelResponse.getCode().equals(CodeSuccess)) {
                userShopModel.getUserModel().setRole(inviteModelResponse.getData().getRole());
                Number responseValue = insertUser(userShopModel.getUserModel());
                if (responseValue != null && responseValue.intValue() > 0) {
                    userShopModel.getUserModel().setId(responseValue.intValue());
                    updateShop(userShopModel);
                    deleteInvite(userShopModel);
                    response = verifySeller(userShopModel.getUserModel());
                } else {
                    Response<UserModel> userModelResponse = getUserIdByMobile(userShopModel.getUserModel().getMobile());
                    if (userModelResponse != null) {
                        userShopModel.getUserModel().setId(userModelResponse.getData().getId());
                        Response<String> updateRoleResponse = updateRole(userShopModel.getUserModel().getId(), inviteModelResponse.getData().getRole());
                        if (updateRoleResponse.getCode().equals(CodeSuccess)) {
                            updateShop(userShopModel);
                            deleteInvite(userShopModel);
                            response = verifySeller(userShopModel.getUserModel());
                        } else {
                            response.setCode(ErrorLog.UDNU1153);
                            response.setMessage(UserDetailNotUpdated);
                        }
                    } else {
                        response.setCode(ErrorLog.UDNA1262);
                        response.setMessage(UserDetailNotAvailable);
                    }
                }
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1213);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        if (response.getCode().equals(CodeFailure)) {
            response.setCode(inviteModelResponse.getCode());
            response.setMessage(inviteModelResponse.getMessage());
        }

        return response;
    }

    /**
     * Gets user by mobile.
     *
     * @param mobile String
     * @return the details of the user.
     */
    public Response<UserModel> getUserIdByMobile(String mobile) {
        Response<UserModel> response = new Response<>();
        UserModel userModel = null;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserColumn.mobile, mobile);

        try {
            userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.getUserIdByMobile, parameters, UserRowMapperLambda.userIdRowMapperLambda);
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

    /**
     * Gets shop by user id.
     * Authorized by SHOP_OWNER only.
     *
     * @param shopId Integer
     * @return the details of the workers for the given shop.
     */
    @Override
    public Response<List<UserModel>> getSellerByShopId(Integer shopId) {
        Response<List<UserModel>> userModelResponse = new Response<>();
        List<UserModel> userModelList = null;

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.shopId, shopId);

            try {
                userModelList = namedParameterJdbcTemplate.query(UserQuery.getSellerByShopId, parameters, UserRowMapperLambda.userDetailRowMapperLambda);
            } catch (Exception e) {
                userModelResponse.setCode(ErrorLog.CE1104);
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                if (userModelList != null) {
                    userModelResponse.prioritySet(Priority.LOW);
                    userModelResponse.setCode(userModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                    userModelResponse.setMessage(ErrorLog.Success);
                    userModelResponse.setData(userModelList);
                } else
                    userModelResponse.setCode(ErrorLog.CE1104);
            }
        } catch (Exception e) {
            userModelResponse.setCode(ErrorLog.CE1105);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return userModelResponse;
    }

    /**************************************************/

    /**
     * Updates the user details
     *
     * @param user UserModel
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateUser(UserModel user) {
        Response<String> response = new Response<>();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(UserColumn.name, user.getName())
                .addValue(UserColumn.oauthId, user.getOauthId())
                .addValue(UserColumn.mobile, user.getMobile())
                .addValue(UserColumn.email, user.getEmail())
                .addValue(UserColumn.id, user.getId());

        int result = namedParameterJdbcTemplate.update(UserQuery.updateUser, parameters);
        if (result > 0) {
            response.prioritySet(Priority.LOW);
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
        }

        return response;
    }

    /**
     * Updates the user notification token
     *
     * @param userNotificationModel UserNotificationModel
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.notifToken, DOUBLE_QUOTE + userNotificationModel.getNotificationToken() + DOUBLE_QUOTE)
                    .addValue(UserColumn.id, userNotificationModel.getId());

            int result = namedParameterJdbcTemplate.update(UserQuery.updateUserNotificationToken, parameters);
            if (result > 0) {
                response.prioritySet(Priority.LOW);
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            } else {
                response.setCode(ErrorLog.UDNU1159);
                response.setMessage(ErrorLog.UserDetailNotUpdated);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1206);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Updates the user role.
     *
     * @param id   Integer
     * @param role UserRole
     * @return success response if the update is successful.
     */
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

    /**
     * Update place details.
     *
     * @param userPlaceModel UserPlaceModel
     * @return success response if the update is successful.
     */
    public void updatePlace(UserPlaceModel userPlaceModel) {
        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserPlaceColumn.userId, userPlaceModel.getUserModel().getId())
                    .addValue(UserPlaceColumn.placeId, userPlaceModel.getPlaceModel().getId());

            int result = namedParameterJdbcTemplate.update(UserPlaceQuery.updatePlaceById, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserPlaceQuery.insertUserPlace, parameters);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Links the user to the given shop
     *
     * @param userShopModel userShopModel
     * @return success response if the update is successful.
     */
    public void updateShop(UserShopModel userShopModel) {
        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.userId, userShopModel.getUserModel().getId())
                    .addValue(UserShopColumn.shopId, userShopModel.getShopModel().getId());

            int result = namedParameterJdbcTemplate.update(UserShopQuery.updateShopById, parameters);
            if (result <= 0)
                namedParameterJdbcTemplate.update(UserShopQuery.insertUserShop, parameters);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Updates the user details and
     * links the user to the given place.
     *
     * @param userPlaceModel UserPlaceModel
     * @return success response if the update is successful.
     */
    @Override
    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel) {
        Response<String> response = updateUser(userPlaceModel.getUserModel());
        updatePlace(userPlaceModel);
        return response;
    }

    /**
     * Deletes the workers in the shop
     * Authorized by SHOP_OWNER only.
     *
     * @param shopId Integer
     * @param userId Integer
     * @return success response if the delete is successful.
     */
    @Override
    public Response<String> deleteSeller(Integer shopId, Integer userId) {
        Response<String> response = new Response<>();
        response.prioritySet(Priority.HIGH);

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.userId, userId)
                    .addValue(UserShopColumn.shopId, shopId);

            int result = namedParameterJdbcTemplate.update(UserShopQuery.deleteUser, parameters);
            if (result > 0) {
                response.prioritySet(Priority.LOW);
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            } else {
                response.setCode(ErrorLog.UDND1164);
                response.setMessage(UnableToDeleteSeller);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1106);
            response.setData(Failure);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Deletes the new user invite sent
     * Authorized by SHOP_OWNER only.
     *
     * @param userShopModel UserShopModel
     * @return success response if the delete is successful.
     */
    @Override
    public Response<String> deleteInvite(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(Column.UserInviteColumn.mobile, userShopModel.getUserModel().getMobile())
                    .addValue(Column.UserInviteColumn.role, userShopModel.getUserModel().getRole().name())
                    .addValue(Column.UserInviteColumn.shopId, userShopModel.getShopModel().getId());

            int responseValue = namedParameterJdbcTemplate.update(Query.UserInviteQuery.deleteInvite, parameters);
            if (responseValue > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            } else {
                response.setCode(ErrorLog.UDND1162);
                response.setMessage(UnableToDeleteInvite);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1161);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }
}
