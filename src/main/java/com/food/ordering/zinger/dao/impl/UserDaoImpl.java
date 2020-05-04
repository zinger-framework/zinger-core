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
import com.food.ordering.zinger.model.notification.UserNotificationModel;
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
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
            response.setData(ErrorLog.Success);
            response.prioritySet(Priority.LOW);
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
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
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
    public Response<String> insertUserShop(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        try {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserShopColumn.userId, userShopModel.getUserModel().getId())
                    .addValue(UserShopColumn.shopId, userShopModel.getShopModel().getId());

            int result = namedParameterJdbcTemplate.update(UserShopQuery.insertUserShop, parameters);
            if (result > 0) {
                response.setCode(CodeSuccess);
                response.setMessage(Success);
                response.setData(Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return response;
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
}
