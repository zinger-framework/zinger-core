package com.food.ordering.zinger.service.interfaces;

import com.food.ordering.zinger.dao.impl.UserDaoImpl;
import com.food.ordering.zinger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    Response<UserPlaceModel> loginRegisterCustomer(UserModel user);

    Response<UserShopListModel> verifySeller(UserModel user);

    Response<String> inviteSeller(UserShopModel userShopModel, String oauthId, Integer id, String role);

    Response<String> acceptInvite(UserShopModel userShopModel);

    /**************************************************/

    Response<List<UserModel>> getSellerByShopId(Integer shopId, String oauthId, Integer id, String role);

    Response<UserInviteModel> verifyInvite(Integer shopId, String mobile);

    /**************************************************/

    Response<String> updateUser(UserModel userModel, String oauthId, Integer id, String role);

    Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, String oauthId, Integer id, String role);

    Response<String> deleteSeller(Integer shopId, Integer userId, String oauthId, Integer id, String role);

    Response<String> deleteInvite(UserShopModel userShopModel, String oauthId, Integer id, String role);
}
