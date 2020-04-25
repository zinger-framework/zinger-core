package com.food.ordering.zinger.utils;

import com.food.ordering.zinger.dao.impl.OrderDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    OrderDaoImpl orderDaoImpl;

    /**
     *   This is a scheduled method used to handle the pending transaction. This method will update the latest
     *   transaction status of all pending orders every 2 mins after contacting the payment gateway.
     */
    @Scheduled(fixedDelay = 120000)
    public void updatePendingOrder() {
        orderDaoImpl.updatePendingOrder();
    }


    /**
     *   This is a scheduled method used to handle the refunded transaction . This method will update the latest
     *   refund status once every 24 hours after contacting the payment gateway.
     */
    @Scheduled(fixedDelay = 86400000)
    public void updateRefundedOrder() {
        orderDaoImpl.updatedRefundOrder();
    }
}
