package com.firstcomestore.common.scheduler;

import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOrderStatuses() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        List<Order> ordersToSetOnDelivery = orderRepository.findAllByOrderStatusAndCreatedAtBefore(
            OrderStatus.ORDER_COMPLETE, oneDayAgo);
        for (Order order : ordersToSetOnDelivery) {
            order.setOrderStatus(OrderStatus.ON_DELIVERY);
            orderRepository.save(order);
        }

        List<Order> ordersToSetDeliveryComplete = orderRepository.findAllByOrderStatusAndCreatedAtBefore(
            OrderStatus.ON_DELIVERY, twoDaysAgo);
        for (Order order : ordersToSetDeliveryComplete) {
            order.setOrderStatus(OrderStatus.DELIVERY_COMPLETE);
            orderRepository.save(order);
        }
    }
}
