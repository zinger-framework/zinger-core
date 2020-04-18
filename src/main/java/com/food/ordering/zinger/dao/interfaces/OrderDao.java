package com.food.ordering.zinger.dao.interfaces;

import com.food.ordering.zinger.constant.Column.OrderColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.Enums.OrderStatus;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.Enums.UserRole;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.constant.Query.OrderQuery;
import com.food.ordering.zinger.constant.Query.TransactionQuery;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.logger.OrderLogModel;
import com.food.ordering.zinger.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.TransactionRowMapperLambda;
import com.food.ordering.zinger.utils.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.food.ordering.zinger.constant.Column.OrderColumn.*;

public interface OrderDao {
    Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel, RequestHeaderModel requestHeaderModel);

    /**************************************************/

    Response<String> placeOrder(Integer orderId, RequestHeaderModel requestHeaderModel);

    /**************************************************/

    Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount, RequestHeaderModel requestHeaderModel);

    Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount, RequestHeaderModel requestHeaderModel);

    Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId, RequestHeaderModel requestHeaderModel);

    Response<TransactionModel> getOrderById(Integer orderId, RequestHeaderModel requestHeaderModel);

    /**************************************************/

    Response<String> updateOrderRating(OrderModel orderModel, RequestHeaderModel requestHeaderModel);

    Response<String> updateOrderStatus(OrderModel orderModel, RequestHeaderModel requestHeaderModel);
}
