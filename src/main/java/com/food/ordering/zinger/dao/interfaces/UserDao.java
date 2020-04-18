package com.food.ordering.zinger.dao.interfaces;

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
import com.food.ordering.zinger.rowMapperLambda.UserInviteRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.UserPlaceRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.UserRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.UserShopRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.*;

public interface UserDao {
    Response<UserPlaceModel> loginRegisterCustomer(UserModel user);

    Response<UserShopListModel> verifySeller(UserModel user);

    Response<UserInviteModel> verifyInvite(Integer shopId, String mobile);

    Response<String> inviteSeller(UserShopModel userShopModel, RequestHeaderModel requestHeaderModel);

    Response<String> acceptInvite(UserShopModel userShopModel);

    /**************************************************/

    Response<List<UserModel>> getSellerByShopId(Integer shopId, RequestHeaderModel requestHeaderModel);

    /**************************************************/

    Response<String> updateUser(UserModel user, RequestHeaderModel requestHeaderModel);

    Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, RequestHeaderModel requestHeaderModel);

    Response<String> deleteSeller(Integer shopId, Integer userId, RequestHeaderModel requestHeaderModel);

    Response<String> deleteInvite(UserShopModel userShopModel, RequestHeaderModel requestHeaderModel);
}
