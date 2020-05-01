package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.*;

import java.util.List;

public interface UserDao {
    Response<UserPlaceModel> loginRegisterCustomer(UserModel user);

    Response<UserShopListModel> verifySeller(UserModel user);

    Response<UserModel> verifyInvite(Integer shopId, String mobile);

    Response<String> inviteSeller(UserShopModel userShopModel);

    Response<UserShopListModel> acceptInvite(UserShopModel userShopModel);

    /**************************************************/

    Response<List<UserModel>> getSellerByShopId(Integer shopId);

    /**************************************************/

    Response<String> updateUser(UserModel user);

    Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel);

    Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel);

    Response<String> deleteSeller(Integer shopId, Integer userId);

    Response<String> deleteInvite(UserShopModel userShopModel);
}
