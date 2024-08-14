package com.firstcomestore.domain.order.dto.response;

import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;

public record OrderResponseDTO(
    Long id,
    String deliveryAddress,
    String deliveryContact,
    OrderStatus orderStatus,
    LocalDateTime orderDate
) {
    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
            order.getId(),
            order.getDeliveryAddress(),
            order.getDeliveryContact(),
            order.getOrderStatus(),
            order.getCreatedAt()
        );
    }
}
