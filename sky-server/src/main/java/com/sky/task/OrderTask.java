package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void processTimeoutOrder() {
        LocalDateTime now = LocalDateTime.now();
        log.info("processTimeoutOrder{}", now);

        //查询订单 15分钟未支付的订单 重点是这里的查询条件
        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, now.minusMinutes(15));
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED); //取消订单
                order.setCancelTime(LocalDateTime.now()); //取消时间
                order.setCancelReason("超时未支付,系统自动取消"); //取消原因
                orderMapper.update(order); //更新订单状态
            }
        }
    }

    /**
     * 自动结束配送中订单 每天一次
     */
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    public void processDeliveryOrder() {
        LocalDateTime now = LocalDateTime.now();
        log.info("processDeliveryOrder{}", now);

        //查询订单 派送中的订单
        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, now.minusHours(3)); // 派送中的订单超过3小时 自动结束 也就是前一天的订单
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED); //自动结束
                orderMapper.update(order); //更新订单状态
            }
        }
    }
}
