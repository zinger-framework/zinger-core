package com.food.ordering.zinger.utils;

import com.food.ordering.zinger.dao.impl.OrderDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    OrderDaoImpl orderDaoImpl;

    @Scheduled(fixedDelay = 120000)
    public void updatePendingOrder() {
        orderDaoImpl.updatePendingOrder();
    }

    @Scheduled(fixedDelay = 86400000)
    public void updateRefundedOrder() {
        orderDaoImpl.updatedRefundOrder();
    }
}
