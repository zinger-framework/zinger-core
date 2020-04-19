package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * NotifyDao is responsible for sending notification
 * to the users like SMS, email, push notification, etc.
 *
 * Endpoints starting with "/notify" invoked here.
 */
@Repository
public class NotifyDaoImpl implements NotifyDao {

    /**
     * Sends the SMS notification to the given user
     *
     * @implNote SMS sending code is left empty for the
     * developer convenience.
     *
     * @param userShopModel UserShopModel
     * @return success response if the notification is sent successfully
     */
    @Override
    public Response<String> notifyInvitation(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        //TODO: Send SMS to notify User

        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        return response;
    }
}
