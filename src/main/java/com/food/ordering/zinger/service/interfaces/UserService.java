package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.notification.UserNotificationModel;

import java.util.List;

public interface UserService {
    Response<UserPlaceModel> loginRegisterCustomer(UserModel user);

    Response<UserShopListModel> verifySeller(UserModel user);

    Response<String> inviteSeller(UserShopModel userShopModel);

    Response<UserShopListModel> acceptInvite(UserShopModel userShopModel);

    /**************************************************/

    Response<List<UserModel>> getSellerByShopId(Integer shopId);

    Response<UserModel> verifyInvite(Integer shopId, String mobile);

    /**************************************************/

    Response<String> updateUser(UserModel userModel);

    Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel);

    Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel);

    Response<String> deleteSeller(Integer shopId, Integer userId);

    Response<String> deleteInvite(UserShopModel userShopModel);
}
