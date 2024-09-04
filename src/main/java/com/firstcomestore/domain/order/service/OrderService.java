package com.firstcomestore.domain.order.service;

import com.firstcomestore.common.exception.ErrorCode;
import com.firstcomestore.common.feignclient.ProductServiceClient;
import com.firstcomestore.domain.order.dto.request.OrderRequestDTO;
import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.exception.ExceedMaxPurchaseLimitException;
import com.firstcomestore.domain.order.exception.InsufficientStockException;
import com.firstcomestore.domain.order.exception.LockAcquisitionFailureException;
import com.firstcomestore.domain.order.exception.OrderNotFoundException;
import com.firstcomestore.domain.order.exception.StatusChangeException;
import com.firstcomestore.domain.order.repository.OrderRepository;
import com.firstcomestore.domain.wishlist.entity.WishList;
import com.firstcomestore.domain.wishlist.exception.WishNotFoundException;
import com.firstcomestore.domain.wishlist.repository.WishListRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WishListRepository wishListRepository;
    private final ProductServiceClient productServiceClient;
    private final RedissonClient redissonClient;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrderFromWish(Long userId, Long wishId, OrderRequestDTO orderRequest)
        throws InterruptedException {
        WishList wish = wishListRepository.findByIdAndUserId(wishId, userId)
            .orElseThrow(() -> new WishNotFoundException());

        Long optionId = wish.getOptionId();
        int purchaseQuantity = wish.getQuantity();

        RLock lock = redissonClient.getLock("order-lock:" + optionId);

        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                Integer maxPurchaseLimit = productServiceClient.getMaxPurchaseLimit(optionId)
                    .getBody();
                if (maxPurchaseLimit != null && purchaseQuantity > maxPurchaseLimit) {
                    throw new ExceedMaxPurchaseLimitException();
                }

                int availableStock = productServiceClient.getOptionStock(optionId).getBody();
                if (availableStock < purchaseQuantity) {
                    throw new InsufficientStockException();
                }

                productServiceClient.updateOptionStock(optionId, -purchaseQuantity);

                Order order = Order.builder()
                    .userId(userId)
                    .optionId(optionId)
                    .quantity(purchaseQuantity)
                    .orderStatus(OrderStatus.ORDER_COMPLETE)
                    .deliveryAddress(orderRequest.deliveryAddress())
                    .deliveryContact(orderRequest.deliveryContact())
                    .build();

                orderRepository.save(order);

            } else {
                throw new LockAcquisitionFailureException();
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void cancelOrder(Long orderId) {
        Order order = getOrder(orderId);

        if (order.getOrderStatus() != OrderStatus.ORDER_COMPLETE) {
            throw new StatusChangeException(ErrorCode.CANCEL_NOT_ALLOWED,
                "주문 ID " + orderId + "은 현재 상태에서 취소할 수 없습니다.");
        }

        productServiceClient.updateOptionStock(order.getOptionId(), order.getQuantity());

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

        if (LocalDate.now().isAfter(order.getUpdatedAt().plusDays(1).toLocalDate())) {
            throw new StatusChangeException(ErrorCode.RETURN_NOT_ALLOWED,
                "주문 ID " + order.getId() + "의 반품 가능 기간이 만료되었습니다.");
        }
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

    @Transactional(readOnly = true)
    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
