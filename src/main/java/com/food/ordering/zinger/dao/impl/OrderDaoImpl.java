package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Column;
import com.food.ordering.zinger.constant.Column.OrderColumn;
import com.food.ordering.zinger.constant.Column.OrderItemColumn;
import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.Enums.OrderStatus;
import com.food.ordering.zinger.constant.Enums.Priority;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.OrderItemQuery;
import com.food.ordering.zinger.constant.Query.OrderQuery;
import com.food.ordering.zinger.dao.interfaces.OrderDao;
import com.food.ordering.zinger.exception.GenericException;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.rowMapperLambda.OrderItemListRowMapperLambda;
import com.food.ordering.zinger.rowMapperLambda.OrderRowMapperLambda;
import com.food.ordering.zinger.utils.Helper;
import com.food.ordering.zinger.utils.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.food.ordering.zinger.constant.Column.OrderColumn.*;
import static com.food.ordering.zinger.constant.Sql.PERCENT;

/**
 * OrderDao is responsible for performing CRUD operation related to the order table in the database.
 * End points starting with '/order' starts here
 *
 * @implNote Request Header (RH) parameter is sent in all endpoints
 * to avoid unauthorized access to our service.
 * @implNote All endpoint services are audited for both success and error responses
 * * using "AuditLogDao".
 */
@Repository
@Transactional
public class OrderDaoImpl implements OrderDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    InterceptorDaoImpl interceptorDaoImpl;

    @Autowired
    TransactionDaoImpl transactionDaoImpl;

    @Autowired
    ItemDaoImpl itemDaoImpl;

    @Autowired
    ShopDaoImpl shopDaoImpl;

    @Autowired
    RatingDaoImpl ratingDaoImpl;

    @Autowired
    UserDaoImpl userDaoImpl;

    @Autowired
    PaymentResponse paymentResponse;

    /**
     * Insert order method
     * -> checks for availability of each item and calculates the total order amount.
     * -> Fetches transaction token from the payment gateway
     * -> Order is inserted into the orders's table
     * -> Transaction token along with orderId returned to the user
     *
     * @param orderItemListModel OrderItemModelList
     * @return Transaction token and orderId returned to the user
     */
    @Override
    public Response<TransactionTokenModel> insertOrder(OrderItemListModel orderItemListModel) throws GenericException {
        /*
         *   1. Verify the amount and availability of the items using verify order
         *   2. Generate the transaction token from payment gateway
         *   3. Insert the order
         * */

        Response<TransactionTokenModel> response = new Response<>();
        TransactionTokenModel transactionTokenModel = new TransactionTokenModel();
        OrderModel orderModel = orderItemListModel.getTransactionModel().getOrderModel();

        Response<String> verifyPricingResponse = verifyPricing(orderItemListModel);
        if (verifyPricingResponse.getCode().equals(ErrorLog.CodeSuccess)) {

            String merchantId = verifyPricingResponse.getData();
            Response<String> initiateTransactionResponse = paymentResponse.initiateTransaction(orderModel, merchantId);
            if (initiateTransactionResponse.getCode().equals(ErrorLog.CodeSuccess)) {

                MapSqlParameterSource parameter = new MapSqlParameterSource()
                        .addValue(userId, orderModel.getUserModel().getId())
                        .addValue(shopId, orderModel.getShopModel().getId())
                        .addValue(price, orderModel.getPrice())
                        .addValue(deliveryPrice, orderModel.getDeliveryPrice())
                        .addValue(deliveryLocation, orderModel.getDeliveryLocation())
                        .addValue(cookingInfo, orderModel.getCookingInfo());

                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate());
                simpleJdbcInsert.withTableName(tableName).usingGeneratedKeyColumns(Column.OrderColumn.id);
                Number responseValue = simpleJdbcInsert.executeAndReturnKey(parameter);

                if (responseValue.intValue() <= 0) {
                    response.setCode(ErrorLog.ODNU1275);
                    response.setMessage(ErrorLog.OrderDetailNotUpdated);
                    response.prioritySet(Priority.HIGH);
                } else {
                    orderItemListModel.getTransactionModel().getOrderModel().setId(responseValue.intValue());
                    Response<String> orderInsertResponse = insertOrderItem(orderItemListModel);
                    if (orderInsertResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        transactionTokenModel.setOrderId(responseValue.intValue());
                        transactionTokenModel.setTransactionToken(initiateTransactionResponse.getData());

                        response.setCode(orderInsertResponse.getCode());
                        response.setMessage(orderInsertResponse.getMessage());
                        response.setData(transactionTokenModel);
                        response.prioritySet(Priority.LOW);
                    } else {
                        response.setCode(ErrorLog.OIDNU301);
                        response.setMessage(ErrorLog.OrderItemDetailNotUpdated);
                        response.prioritySet(Priority.HIGH);
                    }
                }
            } else {
                response.setCode(ErrorLog.TTNA1271);
                response.setMessage(ErrorLog.TransactionTokenNotAvailable);
            }
        } else {
            response.setCode(verifyPricingResponse.getCode());
            response.setMessage(verifyPricingResponse.getMessage());
        }

        if (!response.getCode().equals(ErrorLog.CodeSuccess))
            throw new GenericException(response);

        return response;
    }

    /**
     * Insert order item inserts all the items in orderItemModels list in the OrderItem table
     *
     * @param orderItemModelList OrderItemModelList
     * @return success response if insert operation was successful otherwise failure response is returned
     */
    public Response<String> insertOrderItem(OrderItemListModel orderItemModelList) {
        Response<String> response = new Response<>();
        Integer orderId = orderItemModelList.getTransactionModel().getOrderModel().getId();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource();
            for (int i = 0; i < orderItemModelList.getOrderItemsList().size(); i++) {
                OrderItemModel orderItemModel = orderItemModelList.getOrderItemsList().get(i);
                parameter.addValue(OrderItemColumn.orderId + i, orderId)
                        .addValue(OrderItemColumn.itemId + i, orderItemModel.getItemModel().getId())
                        .addValue(OrderItemColumn.quantity + i, orderItemModel.getQuantity())
                        .addValue(OrderItemColumn.price + i, orderItemModel.getPrice());
            }

            int result = namedParameterJdbcTemplate.update(OrderItemQuery.getInsertOrder(orderItemModelList.getOrderItemsList()), parameter);
            if (result > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return response;
    }

    /**************************************************/

    /**
     * Place order
     * -> Verifies the transaction status of the order by contacting the payment gateway
     * -> If the transaction status is success or pending then the transaction details are inserted into the transaction table
     * -> The order status is updated to PLACED or PENDING accordingly.
     *
     * @param orderId Integer
     * @return success response if insert operation was successful otherwise failure response is returned
     */
    @Override
    public Response<String> placeOrder(Integer orderId) {
        /*
         *   1. verify the transaction status api and
         *   2. Insert the transaction in the transaction table
         * */
        Response<String> response = new Response<>();

        try {
            Response<TransactionModel> verifyOrderResponse = verifyOrder(orderId, Constant.transactionFlag);

            if (verifyOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                Response<String> insertTransactionResponse = transactionDaoImpl.insertTransactionDetails(verifyOrderResponse.getData());

                if (insertTransactionResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                    Response<String> updateOrderStatusResponse = updateOrderStatus(verifyOrderResponse.getData().getOrderModel());

                    if (updateOrderStatusResponse.getCode().equals(ErrorLog.CodeSuccess)) {
                        response.setCode(ErrorLog.CodeSuccess);
                        response.setMessage(ErrorLog.Success);
                        response.setData(ErrorLog.Success);
                        response.prioritySet(Priority.LOW);
                    } else {
                        response.setCode(ErrorLog.ODNU1156);
                        response.setMessage(ErrorLog.OrderDetailNotUpdated);
                    }
                } else {
                    response.setCode(ErrorLog.TDNU1264);
                    response.setMessage(ErrorLog.TransactionDetailNotUpdated);
                }
            } else {
                response.setCode(ErrorLog.ODNA1167);
                response.setMessage(ErrorLog.OrderDetailNotAvailable);
                response.prioritySet(Priority.HIGH);
            }
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1109);
            response.prioritySet(Priority.HIGH);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**************************************************/

    /**
     * This method is responsible for fetching orders placed by a given user in a paginated manner. The pageCount determines number of rows to
     * be returned and pageNum determines the offset.
     *
     * @param userId    Integer
     * @param pageNum   Integer
     * @param pageCount Integer
     * @return Returns all the orders along with transaction details and orderItem details
     */
    @Override
    public Response<List<OrderItemListModel>> getOrderByUserId(Integer userId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = new Response<>();
        List<OrderItemListModel> orderItemListModelList = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(Column.UserColumn.id, userId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            orderItemListModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByUserIds, parameter, OrderItemListRowMapperLambda.OrderItemListByUserIdRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1270);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderItemListModelList != null) {
                response.setCode(orderItemListModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderItemListModelList);
                response.prioritySet(Priority.LOW);
            }
        }
        return response;
    }

    @Override
    public Response<List<OrderItemListModel>> getOrderBySearchQuery(Integer shopId, String searchItem, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = new Response<>();
        List<OrderItemListModel> orderItemListModelList = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(Column.searchQuery, PERCENT + searchItem + PERCENT)
                    .addValue(Column.ShopColumn.id, shopId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            orderItemListModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderFilterByShopPaginationIds, parameter, OrderItemListRowMapperLambda.OrderItemListByUserNameOrUserIdRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1269);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderItemListModelList != null) {
                response.setCode(orderItemListModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderItemListModelList);
                response.prioritySet(Priority.LOW);
            }
        }

        return response;
    }

    /**
     * This method is responsible for fetching all orders with status COMPLETED,DELIVERED,CANCELLED_BY_SELLER,CANCELLED_BY_USER,REFUND_INITIATED
     * and REFUND_COMPLETED by the shop in a paginated manner. The pageCount determines number of rows to be returned and
     * pageNum determines the offset.
     *
     * @param shopId    Integer
     * @param pageNum   Integer
     * @param pageCount Integer
     * @return Returns all the orders along with transaction details and orderItem details
     */
    @Override
    public Response<List<OrderItemListModel>> getOrderByShopIdPagination(Integer shopId, Integer pageNum, Integer pageCount) {
        Response<List<OrderItemListModel>> response = new Response<>();
        List<OrderItemListModel> orderItemListModelList = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(Column.ShopColumn.id, shopId)
                    .addValue(OrderQuery.pageNum, (pageNum - 1) * pageCount)
                    .addValue(OrderQuery.pageCount, pageCount);

            orderItemListModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopPaginationIds, parameter, OrderItemListRowMapperLambda.OrderItemListByUserNameOrUserIdRowMapperLambda);

        } catch (Exception e) {
            response.setCode(ErrorLog.CE1274);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderItemListModelList != null) {
                response.setCode(orderItemListModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderItemListModelList);
                response.prioritySet(Priority.LOW);
            }
        }
        return response;
    }

    /**
     * This method is responsible for fetching orders with status PLACED, ACCEPTED, READY
     *
     * @param shopId Integer
     * @return Returns all the orders along with transaction details and orderItem details
     */
    @Override
    public Response<List<OrderItemListModel>> getOrderByShopId(Integer shopId) {

        Response<List<OrderItemListModel>> response = new Response<>();
        List<OrderItemListModel> orderItemListModelList = null;
        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(Column.ShopColumn.id, shopId);
            orderItemListModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByShopIds, parameter, OrderItemListRowMapperLambda.OrderItemListByUserNameOrUserIdRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1274);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderItemListModelList != null) {
                response.setCode(orderItemListModelList.isEmpty() ? ErrorLog.CodeEmpty : ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderItemListModelList);
                response.prioritySet(Priority.LOW);
            }
        }
        return response;
    }

    /**
     * This method is responsible for fetching orders with status passed in the orderStatusList
     *
     * @param orderStatusList List<OrderStatus>
     * @return Returns all the orders along with transaction details and orderItem details
     */
    private Response<List<OrderModel>> getOrdersByStatus(List<OrderStatus> orderStatusList) {

        Response<List<OrderModel>> response = new Response<>();
        List<OrderModel> orderModelList = null;

        try {
            if (orderStatusList != null && orderStatusList.size() > 0)
                orderModelList = namedParameterJdbcTemplate.query(OrderQuery.getOrderByStatus(orderStatusList), OrderRowMapperLambda.orderRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.ODNA1299);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderModelList != null && !orderModelList.isEmpty()) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderModelList);
            }
        }

        return response;
    }

    /**
     * This method is responsible for fetching orders with given orderId along with its transaction details
     *
     * @param orderId Integer
     * @return Returns all transaction and order details if orderId is found in the database
     */
    @Override
    public Response<OrderItemListModel> getOrderById(Integer orderId) {
        Response<OrderItemListModel> response = new Response<>();
        OrderItemListModel orderItemListModel = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(Column.OrderColumn.id, orderId);
            orderItemListModel = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderByOrderIds, parameter,
                    OrderItemListRowMapperLambda.OrderItemListModelByOrderIdRowMapper);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1279);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderItemListModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderItemListModel);
            }
        }

        return response;
    }

    /**
     * This method responsible for fetching orders with given orderId
     *
     * @param id Integer
     * @return order details if orderId is found in the database
     */
    public Response<OrderModel> getOrderPriceById(Integer id) {
        Response<OrderModel> response = new Response<>();
        OrderModel orderModel = null;

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.id, id);

            orderModel = namedParameterJdbcTemplate.queryForObject(OrderQuery.getOrderPriceById, parameter, OrderRowMapperLambda.orderPriceRowMapperLambda);
        } catch (Exception e) {
            response.setCode(ErrorLog.CE1279);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (orderModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(orderModel);
            }
        }
        return response;
    }

    /**************************************************/

    /**
     * This model updates the rating for a given order.
     *
     * @param orderModel orderModel
     * @return If the update operation is success then success response is returned
     * @implNote Database trigger is used for updating the rating of the shop which serviced the order
     */
    @Override
    public Response<String> updateOrderRating(OrderModel orderModel) {
        Response<String> response = new Response<>();
        response.prioritySet(Priority.HIGH);

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.rating, orderModel.getRating())
                    .addValue(feedback, orderModel.getFeedback())
                    .addValue(id, orderModel.getId());

            int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderRating, parameter);
            if (updateStatus > 0) {
                ratingDaoImpl.updateShopRating(orderModel.getShopModel().getId(), orderModel.getRating());

                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        if (!response.getCode().equals(ErrorLog.CodeSuccess)) {
            response.setCode(ErrorLog.ODNU1285);
            response.setMessage(ErrorLog.OrderDetailNotUpdated);
        }

        return response;
    }

    /**
     * Update order key response updates the secret key in the order table.
     *
     * @param orderModel OrderModel
     * @return success response is returned if update operation is successful
     * @implNote secret key is generated when the status of the order is updated to READY or COMPLETED
     */
    public Response<String> updateOrderKey(OrderModel orderModel) {
        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource()
                    .addValue(OrderColumn.secretKey, orderModel.getSecretKey())
                    .addValue(id, orderModel.getId());

            int updateStatus = namedParameterJdbcTemplate.update(OrderQuery.updateOrderKey, parameter);
            if (updateStatus > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * Update order status
     * -> Checks if the order status change is valid
     * -> If new state is READY or OUT_FOR_DELIVERY then secret key is generated and updated
     * -> If new state is COMPLETED or DELIVERED then secret key sent is checked with secret key in database to validate status change
     * -> If new state is CANCELLED_BY_SELLER or CANCELLED_BY_USER then refund is initiated
     * -> The new state is updated in the database
     *
     * @param orderModel the order model
     * @return success response is returned if update operation is successful
     */
    @Override
    public Response<String> updateOrderStatus(OrderModel orderModel) {
        Response<String> response = new Response<>();
        response.prioritySet(Priority.HIGH);

        try{
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(namedParameterJdbcTemplate.getJdbcTemplate())
                    .withProcedureName(Constant.OrderStatusUpdate.procedureName);

            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue(Constant.OrderStatusUpdate.orderId,orderModel.getId())
                    .addValue(Constant.OrderStatusUpdate.newStatus, orderModel.getOrderStatus())
                    .addValue(Constant.OrderStatusUpdate.newSecretKey, orderModel.getSecretKey());

            Map<String, Object> out = jdbcCall.execute(in);
            Integer result = (Integer) out.get(Constant.OrderStatusUpdate.result);

            if(result == 1){
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(ErrorLog.Success);
                response.prioritySet(Priority.LOW);
            }else if(result == -4){
                response.setCode(ErrorLog.SKM1281);
                response.setMessage(ErrorLog.SecretKeyMismatch);
            }else{
                response.setCode(ErrorLog.IOS1282);
                response.setMessage(ErrorLog.InvalidOrderStatus);
            }


        }catch (Exception e){
            response.setCode(ErrorLog.CE1284);
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    /**
     * This method is a helper function to update the order status
     */
    @Override
    public void updatePendingOrder() {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.PENDING);
        Response<List<OrderModel>> pendingOrderResponse = getOrdersByStatus(orderStatuses);

        if (pendingOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
            List<OrderModel> orderModelList = pendingOrderResponse.getData();

            if (orderModelList != null && !orderModelList.isEmpty()) {
                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = verifyOrder(orderModel.getId(), Constant.transactionFlag);

                    if (transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.PLACED)) {
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - orderModel.getDate().getTime();
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000);
                        int diffInDays = (int) ((currentDate.getTime() - orderModel.getDate().getTime()) / (1000 * 60 * 60 * 24));

                        if (diffMinutes > 10 || diffHours >= 1 || diffInDays >= 1)
                            transactionModelResponse.getData().getOrderModel().setOrderStatus(OrderStatus.REFUND_INITIATED);
                    }

                    if (!transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.PENDING)) {
                        updateOrderStatus(transactionModelResponse.getData().getOrderModel());
                        transactionDaoImpl.updatePendingTransaction(transactionModelResponse.getData());
                    }
                }
            }
        }
    }

    /**
     * This is a helper method to update the refund order
     */
    @Override
    public void updatedRefundOrder() {
        List<OrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderStatus.REFUND_INITIATED);
        orderStatuses.add(OrderStatus.CANCELLED_BY_USER);
        orderStatuses.add(OrderStatus.CANCELLED_BY_SELLER);
        Response<List<OrderModel>> pendingOrderResponse = getOrdersByStatus(orderStatuses);

        if (pendingOrderResponse.getCode().equals(ErrorLog.CodeSuccess)) {
            List<OrderModel> orderModelList = pendingOrderResponse.getData();

            if (orderModelList != null && orderModelList.size() > 0) {
                for (OrderModel orderModel : orderModelList) {
                    Response<TransactionModel> transactionModelResponse = verifyOrder(orderModel.getId(), Constant.refundFlag);

                    if (!transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.REFUND_INITIATED) &&
                            !transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.CANCELLED_BY_SELLER) &&
                            !transactionModelResponse.getData().getOrderModel().getOrderStatus().equals(OrderStatus.CANCELLED_BY_USER)) {
                        updateOrderStatus(transactionModelResponse.getData().getOrderModel());
                        transactionDaoImpl.updatePendingTransaction(transactionModelResponse.getData());
                    }
                }
            }
        }
    }

    /**
     * This method contacts the payment gateway to check the status of transaction or status of refund.
     * If Flag is 1 then refund Status is checked else transaction status is checked.
     *
     * @param orderId Integer
     * @param flag    Integer
     * @return returns the status of the order after contacting the payment gateway
     */
    private Response<TransactionModel> verifyOrder(Integer orderId, String flag) {
        Response<TransactionModel> response = new Response<>();
        TransactionModel transactionModel;

        try {
            Response<TransactionModel> transactionModelResponse;

            if (flag.equals(Constant.refundFlag))
                transactionModelResponse = paymentResponse.getRefundStatus(orderId);
            else
                transactionModelResponse = paymentResponse.getTransactionStatus(orderId);

            Response<OrderModel> orderModelResponse = getOrderPriceById(orderId);

            if (transactionModelResponse.getCode().equals(ErrorLog.CodeSuccess) &&
                    orderModelResponse.getCode().equals(ErrorLog.CodeSuccess)
                //TODO: Uncomment After PAYMENT GATEWAY INTEGRATION
                //&& orderModelResponse.getData().getPrice().equals(transactionModelResponse.getData().transactionAmountGet())
            ) {
                transactionModel = transactionModelResponse.getData();
                orderModelResponse.getData().setOrderStatus(paymentResponse.getOrderStatus(transactionModel));
                transactionModel.setOrderModel(orderModelResponse.getData());

                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(transactionModel);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return response;
    }

    private Response<String> verifyPricing(OrderItemListModel orderItemListModel) {
        Response<String> response = new Response<>();

        String orderTypeFlag = (orderItemListModel.getTransactionModel().getOrderModel().getDeliveryLocation() == null) ? Constant.pickUpOrderFlag : Constant.deliveryOrderFlag;
        String inputJson = Helper.toOrderItemJsonString(orderItemListModel.getOrderItemsList());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(namedParameterJdbcTemplate.getJdbcTemplate())
                .withProcedureName(Constant.VerifyPricingProcedure.procedureName);

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue(Constant.VerifyPricingProcedure.itemList, inputJson)
                .addValue(Constant.VerifyPricingProcedure.shopId, orderItemListModel.getTransactionModel().getOrderModel().getShopModel().getId())
                .addValue(Constant.VerifyPricingProcedure.orderType, orderTypeFlag);

        Map<String, Object> out = jdbcCall.execute(in);
        Integer totalPrice = (Integer) out.get(Constant.VerifyPricingProcedure.totalPrice);
        String merchantId = (String) out.get(Constant.VerifyPricingProcedure.merchantId);

        if (totalPrice != null) {
            if (totalPrice < 0) {
                switch (totalPrice) {
                    case -1:
                        response.setCode(ErrorLog.RNAOC1266);
                        response.setMessage(ErrorLog.RestaurantNotAcceptingOrders);
                        break;
                    case -2:
                        response.setCode(ErrorLog.DONA1263);
                        response.setMessage(ErrorLog.DeliveryOptionNotAvailable);
                        break;
                    case -3:
                        response.setCode(ErrorLog.INA1296);
                        response.setMessage(ErrorLog.ItemsNotAvailable);
                        break;
                }
            } else if (totalPrice != orderItemListModel.getTransactionModel().getOrderModel().getPrice().intValue()) {
                response.setCode(ErrorLog.OPM1300);
                response.setMessage(ErrorLog.OrderPriceMismatch);
                response.prioritySet(Priority.HIGH);
            } else {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(merchantId);
            }
        }

        return response;
    }

    /**
     * Helper method to check the order state change validity. The valid state changes are mentioned below.
     * <p>
     * starting states -> FAILURE,PENDING,PLACED
     * terminal states -> CANCELLED_BY_SELLER,CANCELLED_BY_USER, DELIVERED, COMPLETED
     * <p>
     * Valid state changes
     * PENDING -> FAILURE ,PLACED
     * PLACED  -> CANCELLED_BY_SELLER,CANCELLED_BY_USER , ACCEPTED
     * CANCELLED_BY_SELLER,CANCELLED_BY_USER -> refund table entry must be added
     * ACCEPTED -> READY, OUT_FOR_DELIVERY , CANCELLED_BY_SELLER -> refund table entry must be added
     * READY -> secret key must be updated in table, COMPLETED
     * OUT_FOR_DELIVERY -> secret key must be updated in table, DELIVERED
     *
     * @param currentStatus the current status
     * @param newStatus     the new status
     * @return True is returned if the state change is valid else false
     */
    private boolean checkOrderStatusValidity(OrderStatus currentStatus, OrderStatus newStatus, String deliveryLocation) {
        if (currentStatus == null)
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PENDING) || newStatus.equals(OrderStatus.PLACED);

        else if (currentStatus.equals(OrderStatus.PENDING))
            return newStatus.equals(OrderStatus.TXN_FAILURE) || newStatus.equals(OrderStatus.PLACED) || newStatus.equals(OrderStatus.REFUND_INITIATED);

        else if (currentStatus.equals(OrderStatus.PLACED))
            return newStatus.equals(OrderStatus.CANCELLED_BY_SELLER) || newStatus.equals(OrderStatus.CANCELLED_BY_USER) || newStatus.equals(OrderStatus.ACCEPTED);

        else if (currentStatus.equals(OrderStatus.ACCEPTED)) {
            if (deliveryLocation == null)
                return newStatus.equals(OrderStatus.READY) || newStatus.equals(OrderStatus.CANCELLED_BY_SELLER);
            else
                return newStatus.equals(OrderStatus.OUT_FOR_DELIVERY) || newStatus.equals(OrderStatus.CANCELLED_BY_SELLER);
        } else if (currentStatus.equals(OrderStatus.READY))
            return newStatus.equals(OrderStatus.COMPLETED);

        else if (currentStatus.equals(OrderStatus.OUT_FOR_DELIVERY))
            return newStatus.equals(OrderStatus.DELIVERED);

        else if (currentStatus.equals(OrderStatus.CANCELLED_BY_USER) || currentStatus.equals(OrderStatus.CANCELLED_BY_SELLER) || currentStatus.equals(OrderStatus.REFUND_INITIATED))
            return newStatus.equals(OrderStatus.REFUND_COMPLETED);

        return false;
    }
}
