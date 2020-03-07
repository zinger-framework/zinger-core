package com.food.ordering.ssn.service;

import com.food.ordering.ssn.dao.UserDao;
import com.food.ordering.ssn.model.UserCollegeModel;
import com.food.ordering.ssn.model.UserModel;
import com.food.ordering.ssn.model.UserShopListModel;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public Response<UserCollegeModel> insertCustomer(UserModel user) {
        return userDao.insertCustomer(user);
    }

    public Response<UserShopListModel> insertSeller(UserModel user) {
        return userDao.insertSeller(user);
    }

    /**************************************************/

    public Response<String> updateUserCollegeData(UserCollegeModel userCollegeModel, String oauthId, String mobile) {
        return userDao.updateUserCollegeData(userCollegeModel, oauthId, mobile);
    }
}
