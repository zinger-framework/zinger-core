package com.food.ordering.zinger.notification;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.model.NotificationModel;
import com.food.ordering.zinger.model.Response;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

@Repository
public class PushNotification {

    public PushNotification() {
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
            e.printStackTrace();
        }
    }

    /*
     *   Customer side:
     *
     *   1. web view    -> url type,title,message  -> topic messaging
     *   2. order status  -> order id , order status , restaurant name -> user specific
     *   3. new arrivals and introductions -> shop id -> topic messaging
     *
     *   Seller side:
     *
     *   4. New Order -> order id, amount , No of items,order_status , UserName -> topic
     *   5. Cancel -> order id, amount, No of items , UserName  -> topic
     *
     *
     *
     *   Notification Structure
     *   "notification_data":{
     *       "type": "URL,ORDER_STATUS,NEW_ARRIVALS,NEW_ORDER,ORDER_CANCELLED,NEW_SELLER"
     *       "title": ""
     *       "message":""
     *       "payload": {
     *                       e.g: URL payload
     *                  }
     *   }
     *
     *   URL Payload
     *   {
     *       "url":"www.google.com"
     *   }
     *
     *   ORDER_STATUS Payload
     *   {
     *       "order_id": "O001",
     *       "order_status": "PLACED",
     *       "shop_name" : "Sathyas"
     *   }
     *
     *   NEW_ARRIVALS Payload
     *   {
     *       "shop_id": "1"
     *       "shop_name": "Snow Qube"
     *   }
     *
     *   NEW_ORDER Payload
     *   {
     *       "user_name": "Harsha"
     *       "order_id": "O001",
     *       "amount" : 54,
     *       "items_name": "idly*1,dosa*3"
     *       "order_type": "pickup"
     *   }
     *
     *   ORDER_CANCELLED Payload
     *    {
     *       "user_name": "Harsha"
     *       "order_id": "O001",
     *       "amount" : 54,
     *       "items_name": "idly*1,dosa*3"
     *       "order_type": "pickup"
     *    }
     *
     * */

    public Response<String> sendMulticast() {
        Response<String> response = new Response<>();

        try {
            List<String> registrationTokens = Collections.singletonList("foJdPp1yTzmNbNCaDgBC6e:APA91bEOWbXEORRbH8_RA4C5vbB7TDgmhdYP8dsX3pIMAJsFKiGlzA2irx4TaoEV3oaoWskRVHQvc4hsVZ6IrVQVz4DaCgZ8fBP8qtK9zkUBFaKDl0V-ZXcuQ0XOGGs8X6KIibdkyeBI");
            MulticastMessage message = MulticastMessage.builder()
                    .putData("score", "850")
                    .addAllTokens(registrationTokens)
                    .build();

            BatchResponse fbResponse = FirebaseMessaging.getInstance().sendMulticast(message);
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response<String> sendMulticast(NotificationModel notificationModel,List<String> fcmTokenList) {
        Response<String> response = new Response<>();

        try {
            List<String> registrationTokens = Collections.singletonList("foJdPp1yTzmNbNCaDgBC6e:APA91bEOWbXEORRbH8_RA4C5vbB7TDgmhdYP8dsX3pIMAJsFKiGlzA2irx4TaoEV3oaoWskRVHQvc4hsVZ6IrVQVz4DaCgZ8fBP8qtK9zkUBFaKDl0V-ZXcuQ0XOGGs8X6KIibdkyeBI");
            MulticastMessage message = MulticastMessage.builder()
                    .putData("notification_data",new GsonBuilder().create().toJson(notificationModel))
                    .addAllTokens(fcmTokenList)
                    .build();
            BatchResponse fbResponse = FirebaseMessaging.getInstance().sendMulticast(message);
            response.setCode(ErrorLog.CodeSuccess);
            response.setMessage(ErrorLog.Success);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return response;
    }
}