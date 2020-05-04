package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.notification.CustomerPayLoad;
import com.food.ordering.zinger.model.notification.NotificationModel;
import com.food.ordering.zinger.model.notification.SellerPayLoad;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * NotifyDao is responsible for sending notification
 * to the users like SMS, email, push notification, etc.
 * <p>
 * Endpoints starting with "/notify" invoked here.
 */
@Repository
public class NotifyDaoImpl implements NotifyDao {

    private FirebaseMessaging firebaseMessaging;

    /**
     * Init fire base notifications.
     */
    @Bean
    void initFireBaseNotifications() {
        FileInputStream serviceAccount = null;
        FirebaseOptions options = null;
        try {
            serviceAccount = new FileInputStream("src/main/resources/zinger-fb-adminsdk.json");
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://zinger-58902.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        firebaseMessaging = FirebaseMessaging.getInstance();
    }

    /**
     * Send notification message to all the multiple clients using FCM token list. Success response if
     * all messages is received successfully
     *
     * @param notificationModel the notification model
     * @param fcmTokenList      the fcm token list
     * @return the response
     */
    Response<String> sendMulticast(NotificationModel notificationModel, List<String> fcmTokenList) {
        Response<String> response = new Response<>();

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .putData(Constant.notificationTitle, notificationModel.getTitle())
                    .putData(Constant.notificationMessage, notificationModel.getMessage())
                    .putData(Constant.notificationType, notificationModel.getType().name())
                    .putData(Constant.notificationPayload, notificationModel.getPayload())
                    .addAllTokens(fcmTokenList)
                    .build();

            if (firebaseMessaging == null)
                firebaseMessaging = FirebaseMessaging.getInstance();
            BatchResponse fbResponse = firebaseMessaging.sendMulticast(message);

            if (fbResponse.getSuccessCount() > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Send notification message to all clients subscribed to a topic. Success response if all subscribed clients
     * receive the message
     * @param notificationModel the notification model
     * @param topic             the topic
     * @return the response
     */
    Response<String> sendTopicMessage(NotificationModel notificationModel, String topic) {
        Response<String> response = new Response<>();
        try {
            Message message = Message.builder()
                    .putData(Constant.notificationTitle, notificationModel.getTitle())
                    .putData(Constant.notificationMessage, notificationModel.getMessage())
                    .putData(Constant.notificationType, notificationModel.getType().name())
                    .putData(Constant.notificationPayload, notificationModel.getPayload())
                    .setTopic(topic)
                    .build();

            if (firebaseMessaging == null)
                firebaseMessaging = FirebaseMessaging.getInstance();
            firebaseMessaging.send(message);

            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Send notification message to all signed in users. Success response if all targeted users receive the message.
     * Success response if all notifications are sent succesfully
     * @param notificationModel the notification model
     * @return the response
     */
    @Override
    public Response<String> sendGlobalNotification(NotificationModel notificationModel) {
        return sendTopicMessage(notificationModel, Constant.globalNotificationTopic);
    }

    /**
     * Send notification message to the customer and all the sellers of a given shop when order status changes
     * Success response if all notifications are sent succesfully
     *
     * @param response Response<OrderItemListModel>
     * @return the response
     */
    @Override
    public void notifyOrderStatus(Response<OrderItemListModel> response) {
        if (response.getCode().equals(ErrorLog.CodeSuccess)) {
            OrderItemListModel orderItemListModel = response.getData();

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("");
            notificationModel.setMessage("");
            notificationModel.setType(Enums.NotificationType.SELLER_ORDER_STATUS);

            CustomerPayLoad customerPayLoad = new CustomerPayLoad();
            OrderModel orderModel = orderItemListModel.getTransactionModel().getOrderModel();
            List<OrderStatusModel> orderStatusModel = orderItemListModel.getOrderStatusModel();
            customerPayLoad.setOrderId(orderModel.getId());
            customerPayLoad.setOrderStatus(orderStatusModel.get(orderStatusModel.size() - 1).getOrderStatus());
            customerPayLoad.setShopName(orderModel.getShopModel().getName());
            customerPayLoad.setSecretKey(orderModel.getSecretKey());

            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(customerPayLoad);
            notificationModel.setPayload(jsonPayload);

            sendMulticast(notificationModel, orderItemListModel.getTransactionModel().getOrderModel().getUserModel().getNotificationToken());

            customerPayLoad.setSecretKey(null);
            jsonPayload = gson.toJson(customerPayLoad);
            notificationModel.setPayload(jsonPayload);
            ShopModel shopModel = orderItemListModel.getTransactionModel().getOrderModel().getShopModel();
            sendTopicMessage(notificationModel, "zinger" + shopModel.getId());
        }
    }

    /**
     * Send notification message to the seller when customer places or cancels an order
     * Success response if all notifications are sent succesfully
     *
     * @param response Response<OrderItemListModel>
     * @return the response
     */
    @Override
    public void notifyOrderStatusToSeller(Response<OrderItemListModel> response) {
        if (response.getCode().equals(ErrorLog.CodeSuccess)) {
            OrderItemListModel orderItemListModel = response.getData();
            List<OrderStatusModel> orderStatusModelList = response.getData().getOrderStatusModel();

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("");
            notificationModel.setMessage("");
            notificationModel.setType(Enums.NotificationType.USER_ORDER_STATUS);

            SellerPayLoad sellerPayLoad = new SellerPayLoad();
            sellerPayLoad.setOrderId(orderItemListModel.getTransactionModel().getOrderModel().getId());
            sellerPayLoad.setAmount(orderItemListModel.getTransactionModel().getOrderModel().getPrice());
            sellerPayLoad.setUserName(orderItemListModel.getTransactionModel().getOrderModel().getUserModel().getName());
            sellerPayLoad.setOrderStatus(orderStatusModelList.get(orderStatusModelList.size() - 1).getOrderStatus());
            if (orderItemListModel.getTransactionModel().getOrderModel().getDeliveryLocation() != null)
                sellerPayLoad.setOrderType(Constant.deliveryOrderFlag);
            else
                sellerPayLoad.setOrderType(Constant.pickUpOrderFlag);

            ArrayList<String> itemList = new ArrayList<>();
            orderItemListModel.getOrderItemsList().forEach(orderItemModel ->
                    itemList.add(orderItemModel.getItemModel().getName() + " * " + orderItemModel.getQuantity()));
            sellerPayLoad.setItemList(itemList);

            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(sellerPayLoad);
            notificationModel.setPayload(jsonPayload);

            ShopModel shopModel = orderItemListModel.getTransactionModel().getOrderModel().getShopModel();
            sendTopicMessage(notificationModel, "zinger" + shopModel.getId());
        }
    }
}
