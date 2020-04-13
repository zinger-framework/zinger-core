package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotifyDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Response<String> notifyInvitation(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        //TODO: Send SMS to notify User

        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        return response;
    }
}
