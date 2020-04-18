package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.*;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import static com.food.ordering.zinger.constant.Query.AuditLogQuery;

public interface AuditLogDao {
    Response<String> insertPlaceLog(PlaceLogModel placeLogModel);

    Response<String> insertShopLog(ShopLogModel ShopLogModel);

    public Response<String> insertUserLog(UserLogModel UserLogModel);

    public Response<String> insertItemLog(ItemLogModel ItemLogModel);

    public Response<String> insertOrderLog(OrderLogModel OrderLogModel);
}
