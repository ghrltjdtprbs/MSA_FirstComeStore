package com.firstcomestore.common.scheduler;

import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderOption;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.repository.OrderRepository;
import com.firstcomestore.domain.product.entity.Inventory;
import com.firstcomestore.domain.product.repository.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void completeReturns() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        List<Order> ordersToComplete = orderRepository.findAllByOrderStatusAndUpdatedAtBefore(
            OrderStatus.ON_RETURN, oneDayAgo);

        for (Order order : ordersToComplete) {
            for (OrderOption orderOption : order.getOrderOptions()) {
                Inventory inventory = orderOption.getOption().getInventory();

                inventory.setStock(inventory.getStock() + orderOption.getQuantity());
                inventoryRepository.save(inventory);
            }

            order.setOrderStatus(OrderStatus.RETURN_COMPLETE);
            orderRepository.save(order);
        }
    }
}
