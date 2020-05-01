package com.food.ordering.zinger.dao.impl;

import com.food.ordering.zinger.constant.Constant;
import com.food.ordering.zinger.constant.Enums;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.NotifyDao;
import com.food.ordering.zinger.model.*;
import com.food.ordering.zinger.model.notification.NewOrderPayLoad;
import com.food.ordering.zinger.model.notification.OrderStatusPayLoad;
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
    }

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

            BatchResponse fbResponse = FirebaseMessaging.getInstance().sendMulticast(message);

            if (fbResponse.getSuccessCount() > 0) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return response;
    }

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

            FirebaseMessaging.getInstance().send(message);
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Sends the SMS notification to the given user
     *
     * @param userShopModel UserShopModel
     * @return success response if the notification is sent successfully
     * @implNote SMS sending code is left empty for the
     * developer convenience.
     */
    @Override
    public Response<String> notifyInvitation(UserShopModel userShopModel) {
        Response<String> response = new Response<>();

        //TODO: Send SMS to notify User

        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);
        response.setData(ErrorLog.Success);
        return response;
    }

    @Override
    public void notifyNewOrder(Response<OrderItemListModel> response) {
        if (response.getCode().equals(ErrorLog.CodeSuccess)) {
            OrderItemListModel orderItemListModel = response.getData();

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("");
            notificationModel.setMessage("");
            notificationModel.setType(Enums.NotificationType.NEW_ORDER);

            NewOrderPayLoad newOrderPayLoad = new NewOrderPayLoad();
            newOrderPayLoad.setOrderId(orderItemListModel.getTransactionModel().getOrderModel().getId());
            newOrderPayLoad.setAmount(orderItemListModel.getTransactionModel().getOrderModel().getPrice());
            newOrderPayLoad.setUserName(orderItemListModel.getTransactionModel().getOrderModel().getUserModel().getName());

            ArrayList<String> itemList = new ArrayList<>();
            orderItemListModel.getOrderItemsList().stream().forEach(orderItemModel ->
                    itemList.add(orderItemModel.getItemModel().getName() + " * " + orderItemModel.getQuantity()));
            newOrderPayLoad.setItemList(itemList);

            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(newOrderPayLoad);
            notificationModel.setPayload(jsonPayload);

            ShopModel shopModel = orderItemListModel.getTransactionModel().getOrderModel().getShopModel();
            String[] names = shopModel.getName().split(" ");
            sendTopicMessage(notificationModel, names[0] + shopModel.getId());
        }
    }

    @Override
    public void notifyUpdateOrder(Response<OrderItemListModel> response) {
        if (response.getCode().equals(ErrorLog.CodeSuccess)) {
            OrderItemListModel orderItemListModel = response.getData();

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("");
            notificationModel.setMessage("");
            notificationModel.setType(Enums.NotificationType.ORDER_STATUS);

            OrderStatusPayLoad orderStatusPayLoad = new OrderStatusPayLoad();
            OrderModel orderModel = orderItemListModel.getTransactionModel().getOrderModel();
            List<OrderStatusModel> orderStatusModel = orderItemListModel.getOrderStatusModel();
            orderStatusPayLoad.setOrderId(orderModel.getId());
            orderStatusPayLoad.setOrderStatus(orderStatusModel.get(orderStatusModel.size() - 1).getOrderStatus());
            orderStatusPayLoad.setShopName(orderModel.getShopModel().getName());
            orderStatusPayLoad.setSecretKey(orderModel.getSecretKey());

            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(orderStatusPayLoad);
            notificationModel.setPayload(jsonPayload);

            sendMulticast(notificationModel, orderItemListModel.getTransactionModel().getOrderModel().getUserModel().getNotificationToken());

            ShopModel shopModel = orderItemListModel.getTransactionModel().getOrderModel().getShopModel();
            String[] names = shopModel.getName().split(" ");
            sendTopicMessage(notificationModel, names[0] + shopModel.getId());
        }
    }

    @Override
    public void notifyCancelOrderByUser(Response<OrderItemListModel> response) {
        if (response.getCode().equals(ErrorLog.CodeSuccess)) {
            OrderItemListModel orderItemListModel = response.getData();

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("");
            notificationModel.setMessage("");
            notificationModel.setType(Enums.NotificationType.ORDER_CANCELLED);

            NewOrderPayLoad newOrderPayLoad = new NewOrderPayLoad();
            newOrderPayLoad.setOrderId(orderItemListModel.getTransactionModel().getOrderModel().getId());
            newOrderPayLoad.setAmount(orderItemListModel.getTransactionModel().getOrderModel().getPrice());
            newOrderPayLoad.setUserName(orderItemListModel.getTransactionModel().getOrderModel().getUserModel().getName());

            ArrayList<String> itemList = new ArrayList<>();
            orderItemListModel.getOrderItemsList().stream().forEach(orderItemModel ->
                    itemList.add(orderItemModel.getItemModel().getName() + " * " + orderItemModel.getQuantity()));
            newOrderPayLoad.setItemList(itemList);

            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(newOrderPayLoad);
            notificationModel.setPayload(jsonPayload);

            ShopModel shopModel = orderItemListModel.getTransactionModel().getOrderModel().getShopModel();
            String[] names = shopModel.getName().split(" ");
            sendTopicMessage(notificationModel, names[0] + shopModel.getId());
        }
    }

    @Override
    public Response<String> notifyWebView(NotificationModel notificationModel) {
        return sendTopicMessage(notificationModel, Constant.globalNotificationTopic);
    }

    @Override
    public Response<String> notifyNewArrival(NotificationModel notificationModel) {
        return sendTopicMessage(notificationModel, Constant.globalNotificationTopic);
    }
}
