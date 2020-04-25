package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.*;

public interface AuditLogDao {
    Response<String> insertPlaceLog(PlaceLogModel placeLogModel);

    Response<String> insertShopLog(ShopLogModel ShopLogModel);

    Response<String> insertUserLog(UserLogModel UserLogModel);

    Response<String> insertItemLog(ItemLogModel ItemLogModel);

    Response<String> insertOrderLog(OrderLogModel OrderLogModel);
}
