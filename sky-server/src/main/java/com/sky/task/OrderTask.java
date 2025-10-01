package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的定时任务
     */
    @Scheduled(cron = "0 * * * * ? ")   // 每分钟触发一次
    public void processTimeoutOrders() {
//        log.info("处理超时订单");
        // 获取超时订单，即订单创建时间<当前时间-15min
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,
                LocalDateTime.now().plusMinutes(-15));
        // 将超时订单的状态修改为取消状态
        if (!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理一直处于派送中的订单的定时任务
     */
    @Scheduled(cron = "0 0 1 * * ? ")   // 每天凌晨一点触发
    public void processDeliveryOrders() {
//        log.info("处理派送中的订单");
        // 获取一直处于派送中的订单，即订单创建时间<昨天24点
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,
                LocalDateTime.now().plusMinutes(-60));
        // 将一直处于派送中的订单的状态修改为完成状态
        if (!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }
}
