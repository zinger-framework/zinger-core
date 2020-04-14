package com.food.ordering.zinger.utils;

import com.food.ordering.zinger.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    OrderDao orderDao;

    //TODO: Update Delay: 10s -> 60s
    @Scheduled(fixedDelay = 60000)
    public void updatePendingOrder() {
        orderDao.updatePendingOrder();
    }

    @Scheduled(fixedDelay = 86400000)
    public void updateRefundedOrder() {
        orderDao.updatedRefundOrder();
    }
}
