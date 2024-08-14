package com.firstcomestore.domain.order.service;

import com.firstcomestore.common.exception.ErrorCode;
import com.firstcomestore.domain.order.entity.Order;
import com.firstcomestore.domain.order.entity.OrderOption;
import com.firstcomestore.domain.order.entity.OrderStatus;
import com.firstcomestore.domain.order.exception.InsufficientStockException;
import com.firstcomestore.domain.order.exception.OrderNotFoundException;
import com.firstcomestore.domain.order.exception.StatusChangeException;
import com.firstcomestore.domain.order.repository.OrderRepository;
import com.firstcomestore.domain.product.entity.Inventory;
import com.firstcomestore.domain.product.entity.Option;
import com.firstcomestore.domain.product.repository.InventoryRepository;
import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.exception.UserNotFoundException;
import com.firstcomestore.domain.user.repository.UserRepository;
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
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public Order createOrder(Long userId, Long wishlistId, String deliveryAddress,
        String deliveryContact) {
        User user = getUser(userId);
        WishList wish = getWishList(userId, wishlistId);
        Inventory inventory = wish.getOption().getInventory();

        if (inventory.getStock() < wish.getQuantity()) {
            throw new InsufficientStockException();
        }

        Order order = Order.builder()
            .user(user)
            .deliveryAddress(deliveryAddress)
            .deliveryContact(deliveryContact)
            .orderStatus(OrderStatus.ORDER_COMPLETE)
            .build();

        Option option = wish.getOption();
        inventory.setStock(inventory.getStock() - wish.getQuantity());

        OrderOption orderOption = OrderOption.builder()
            .order(order)
            .option(option)
            .quantity(wish.getQuantity())
            .build();

        order.setOrderOptions(List.of(orderOption));
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

        restoreInventory(order);
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

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());
    }

    private WishList getWishList(Long userId, Long wishlistId) {
        return wishListRepository.findByIdAndUserId(wishlistId, userId)
            .orElseThrow(() -> new WishNotFoundException());
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException());
    }

    private void restoreInventory(Order order) {
        for (OrderOption orderOption : order.getOrderOptions()) {
            Inventory inventory = orderOption.getOption().getInventory();
            inventory.setStock(inventory.getStock() + orderOption.getQuantity());
            inventoryRepository.save(inventory);
        }
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
