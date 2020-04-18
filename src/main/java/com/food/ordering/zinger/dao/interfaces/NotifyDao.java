package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserShopModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

public interface NotifyDao {
    Response<String> notifyInvitation(UserShopModel userShopModel);
}
