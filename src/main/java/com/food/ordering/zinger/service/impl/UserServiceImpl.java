package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.impl.AuditLogDaoImpl;
import com.food.ordering.zinger.dao.impl.InterceptorDaoImpl;
import com.food.ordering.zinger.dao.interfaces.*;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.UserLogModel;
import com.food.ordering.zinger.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    InterceptorDao interceptorDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Override
    public Response<UserPlaceModel> loginRegisterCustomer(UserModel user) {
        return userDao.loginRegisterCustomer(user);
    }

    @Override
    public Response<UserShopListModel> verifySeller(UserModel user) {
        return userDao.verifySeller(user);
    }

    @Override
    public Response<String> inviteSeller(UserShopModel userShopModel, String oauthId, Integer id, String role) {
        Response<String> response = new Response<>();
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);

        if (requestHeaderModel.getRole().equals(Enums.UserRole.SHOP_OWNER.name())) {
            if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess)) {
                response.setCode(ErrorLog.IH1027);
                response.setMessage(ErrorLog.InvalidHeader);
            }
            else
                response = userDao.inviteSeller(userShopModel, requestHeaderModel);
        }
        else {
            response.setCode(ErrorLog.IH1061);
            response.setMessage(ErrorLog.InvalidHeader);
        }

        auditLogDao.insertUserLog(new UserLogModel(response, requestHeaderModel.getId(), null, userShopModel.toString()));
        return response;
    }

    @Override
    public Response<UserShopListModel> acceptInvite(UserShopModel userShopModel) {
        return userDao.acceptInvite(userShopModel);
    }

    /**************************************************/

    @Override
    public Response<List<UserModel>> getSellerByShopId(Integer shopId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.getSellerByShopId(shopId, requestHeaderModel);
    }

    @Override
    public Response<UserInviteModel> verifyInvite(Integer shopId, String mobile) {
        return userDao.verifyInvite(shopId, mobile);
    }

    /**************************************************/

    @Override
    public Response<String> updateUser(UserModel userModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.updateUser(userModel, requestHeaderModel);
    }

    @Override
    public Response<String> updateUserPlaceData(UserPlaceModel userPlaceModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.updateUserPlaceData(userPlaceModel, requestHeaderModel);
    }

    @Override
    public Response<String> deleteSeller(Integer shopId, Integer userId, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.deleteSeller(shopId, userId, requestHeaderModel);
    }

    @Override
    public Response<String> deleteInvite(UserShopModel userShopModel, String oauthId, Integer id, String role) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(oauthId, id, role);
        return userDao.deleteInvite(userShopModel, requestHeaderModel);
    }
}
