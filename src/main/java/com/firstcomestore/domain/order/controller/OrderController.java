package com.firstcomestore.domain.order.controller;

import com.firstcomestore.common.jwt.JwtCookieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final JwtCookieService jwtCookieService;

    public OrderController(JwtCookieService jwtCookieService) {
        this.jwtCookieService = jwtCookieService;
    }

    @GetMapping
    public ResponseEntity<String> getOrders(HttpServletRequest request) {
        Long userId = jwtCookieService.getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // userId를 이용한 주문 처리 로직...
        return ResponseEntity.ok("User ID: " + userId);
    }
}
