package com.firstcomestore.domain.order.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.jwt.JwtCookieService;
import com.firstcomestore.domain.order.dto.request.OrderRequestDTO;
import com.firstcomestore.domain.order.dto.response.OrderResponseDTO;
import com.firstcomestore.domain.order.exception.UserNotFoundException;
import com.firstcomestore.domain.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtCookieService jwtCookieService;

    @PostMapping("/{wishlistId}")
    public ResponseEntity<ResponseDTO<Void>> createOrder(@PathVariable Long wishlistId,
        @Valid @RequestBody OrderRequestDTO orderRequest, HttpServletRequest request) {
        Long userId = getUserIdOrThrow(request);
        orderService.createOrder(userId, wishlistId, orderRequest.deliveryAddress(),
            orderRequest.deliveryContact());
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<OrderResponseDTO>>> getOrders(
        HttpServletRequest request) {
        Long userId = getUserIdOrThrow(request);
        List<OrderResponseDTO> response = orderService.getOrders(userId).stream()
            .map(OrderResponseDTO::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ResponseDTO<Void>> cancelOrder(@PathVariable Long orderId,
        HttpServletRequest request) {
        Long userId = getUserIdOrThrow(request);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<ResponseDTO<Void>> returnOrder(@PathVariable Long orderId,
        HttpServletRequest request) {
        Long userId = getUserIdOrThrow(request);
        orderService.returnOrder(orderId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private Long getUserIdOrThrow(HttpServletRequest request) {
        Long userId = jwtCookieService.getUserIdFromRequest(request);
        if (userId == null) {
            throw new UserNotFoundException();
        }
        return userId;
    }
}
