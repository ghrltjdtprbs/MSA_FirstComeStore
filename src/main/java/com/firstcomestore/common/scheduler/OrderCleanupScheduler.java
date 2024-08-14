package com.firstcomestore.common.scheduler;

import com.firstcomestore.domain.order.repository.OrderRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldSoftDeletedOrders() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        int deletedOrdersCount = orderRepository.deleteOldSoftDeletedOrders(oneYearAgo);

        if (deletedOrdersCount > 0) {
            log.info("총 {}개의 주문이 데이터베이스에서 삭제되었습니다.", deletedOrdersCount);
        } else {
            log.info("삭제할 주문이 없습니다.");
        }
    }
}
