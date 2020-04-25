package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;

public interface InterceptorDao {
    Response<UserModel> validateUser(RequestHeaderModel requestHeaderModel);
}
