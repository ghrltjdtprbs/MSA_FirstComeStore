package com.firstcomestore.domain.order.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.domain.order.dto.request.OrderRequestDTO;
import com.firstcomestore.domain.order.dto.response.OrderResponseDTO;
import com.firstcomestore.domain.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{wishlistId}")
    public ResponseEntity<ResponseDTO<Void>> createOrder(@PathVariable Long wishlistId,
        @Valid @RequestBody OrderRequestDTO orderRequest) {
        Long userId = getCurrentUserId();
        orderService.createOrder(userId, wishlistId, orderRequest.deliveryAddress(),
            orderRequest.deliveryContact());
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<OrderResponseDTO>>> getOrders() {
        Long userId = getCurrentUserId();
        List<OrderResponseDTO> response = orderService.getOrders(userId).stream()
            .map(OrderResponseDTO::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseDTO<Void>> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<ResponseDTO<Void>> returnOrder(@PathVariable Long orderId) {
        orderService.returnOrder(orderId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
