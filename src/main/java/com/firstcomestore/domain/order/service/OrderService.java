package com.firstcomestore.domain.order.service;

import com.firstcomestore.domain.order.dto.request.OrderRequestDTO;
import com.firstcomestore.domain.order.entity.Order;
import java.util.List;

public interface OrderService {

    void createOrder(Long userId, Long wishId, OrderRequestDTO orderRequest)
        throws InterruptedException;

    void cancelOrder(Long orderId);

    void returnOrder(Long orderId);

    List<Order> getOrders(Long userId);
}
