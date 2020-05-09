package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.model.UserPlaceModel;
import com.food.ordering.zinger.model.UserShopListModel;
import com.food.ordering.zinger.model.notification.UserNotificationModel;

public interface UserService {
    Response<UserPlaceModel> loginRegisterCustomer(UserModel user);

    Response<UserShopListModel> verifySeller(UserModel user);

    /**************************************************/

    Response<String> updateUser(UserModel userModel);

    Response<String> updateUserNotificationToken(UserNotificationModel userNotificationModel);

    Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel);
}
