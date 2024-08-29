package com.firstcomestore.domain.order.service;

import com.firstcomestore.common.exception.ErrorCode;
import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.exception.OrderNotFoundException;
import com.firstcomestore.domain.order.exception.StatusChangeException;
import com.firstcomestore.domain.order.repository.OrderRepository;
import com.firstcomestore.domain.wishlist.entity.WishList;
import com.firstcomestore.domain.wishlist.exception.WishNotFoundException;
import com.firstcomestore.domain.wishlist.repository.WishListRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishListRepository wishListRepository;

    public Order createOrder(Long userId, Long wishlistId, String deliveryAddress, String deliveryContact) {
        WishList wish = getWishList(userId, wishlistId);

        // 예시로, 재고 관리와 관련된 처리를 제거했습니다.
        // 실제 구현에서는 다른 서비스로부터 재고 정보를 받아오거나, 트랜잭션 없이 처리할 수 있습니다.

        Order order = Order.builder()
            .userId(userId)
            .optionId(wish.getOptionId())
            .deliveryAddress(deliveryAddress)
            .deliveryContact(deliveryContact)
            .quantity(wish.getQuantity())
            .orderStatus(OrderStatus.ORDER_COMPLETE)
            .build();

        orderRepository.save(order);

        wishListRepository.delete(wish);

        return order;
    }

    public void cancelOrder(Long orderId) {
        Order order = getOrder(orderId);

        if (order.getOrderStatus() != OrderStatus.ORDER_COMPLETE) {
            throw new StatusChangeException(ErrorCode.CANCEL_NOT_ALLOWED,
                "주문 ID " + orderId + "은 현재 상태에서 취소할 수 없습니다.");
        }

        // 재고 복구 로직 제거
        updateOrderStatusAndDelete(order, OrderStatus.CANCEL);
    }

    public void returnOrder(Long orderId) {
        Order order = getOrder(orderId);

        validateReturnOrder(order);

        order.setOrderStatus(OrderStatus.ON_RETURN);
        orderRepository.save(order);
    }

    private void validateReturnOrder(Order order) {
        if (order.getOrderStatus() != OrderStatus.DELIVERY_COMPLETE) {
            throw new StatusChangeException(ErrorCode.RETURN_NOT_ALLOWED,
                "주문 ID " + order.getId() + "은 현재 상태에서 반품할 수 없습니다.");
        }

        int returnPeriod = 1;
        if (LocalDate.now().isAfter(order.getUpdatedAt().plusDays(returnPeriod).toLocalDate())) {
            throw new StatusChangeException(ErrorCode.RETURN_NOT_ALLOWED,
                "주문 ID " + order.getId() + "의 반품 가능 기간이 만료되었습니다.");
        }
    }

    private WishList getWishList(Long userId, Long wishlistId) {
        return wishListRepository.findByIdAndUserId(wishlistId, userId)
            .orElseThrow(() -> new WishNotFoundException());
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException());
    }

    private void updateOrderStatusAndDelete(Order order, OrderStatus status) {
        order.setOrderStatus(status);
        order.delete(LocalDateTime.now());
        orderRepository.save(order);
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
