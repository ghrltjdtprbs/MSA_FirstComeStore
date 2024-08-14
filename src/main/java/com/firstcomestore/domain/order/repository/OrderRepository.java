package com.firstcomestore.domain.order.repository;

import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.deletedAt IS NOT NULL AND o.deletedAt < :cutoffDate")
    int deleteOldSoftDeletedOrders(LocalDateTime cutoffDate);

    List<Order> findAllByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus,
        LocalDateTime dateTime);

    List<Order> findAllByOrderStatusAndUpdatedAtBefore(OrderStatus orderStatus,
        LocalDateTime dateTime);

}
