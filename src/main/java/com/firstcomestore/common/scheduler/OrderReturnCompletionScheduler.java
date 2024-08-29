package com.firstcomestore.common.scheduler;

import com.firstcomestore.common.feignclient.ProductServiceClient;
import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderReturnCompletionScheduler {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void completeReturns() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        List<Order> ordersToComplete = orderRepository.findAllByOrderStatusAndUpdatedAtBefore(
            OrderStatus.ON_RETURN, oneDayAgo);

        for (Order order : ordersToComplete) {
            productServiceClient.updateOptionStock(order.getOptionId(), order.getQuantity());

            order.setOrderStatus(OrderStatus.RETURN_COMPLETE);
            orderRepository.save(order);
        }
    }
}
