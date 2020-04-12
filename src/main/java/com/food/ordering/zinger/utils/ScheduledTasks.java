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
    @Scheduled(fixedDelay = 10000)
    public void scheduleTaskWithFixedRate() {
        orderDao.updatePendingOrder();
    }
}
