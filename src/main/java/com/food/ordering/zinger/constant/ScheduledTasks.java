package com.food.ordering.zinger.utils;

import com.food.ordering.zinger.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    @Autowired
    OrderDao orderDao;

    // https://www.callicoder.com/spring-boot-task-scheduling-with-scheduled-annotation/
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Scheduled(fixedDelay = 10000)
    public void scheduleTaskWithFixedRate() {
        System.out.println("testing from "+Thread.currentThread().getName()+" "+dateTimeFormatter.format(LocalDateTime.now()));
        orderDao.updatePendingOrder();
    }
}
