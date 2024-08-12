package com.firstcomestore.domain.user.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.util.CookieUtils;
import com.firstcomestore.domain.user.dto.request.CreateUserRequestDTO;
import com.firstcomestore.domain.user.dto.request.LoginRequestDTO;
import com.firstcomestore.domain.user.dto.request.TokenDTO;
import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    private final CookieUtils cookieUtils;
    @Value("${cookie.domain}")
    private String domain;

    @PostMapping()
    public ResponseEntity<ResponseDTO<Void>> signUp(
        @Valid @RequestBody CreateUserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.signUp(requestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Void>> login(
        @RequestBody @Valid LoginRequestDTO loginRequest,
        HttpServletResponse response
    ) {
        User user = userService.findByEmail(loginRequest.email());
        TokenDTO tokenDTO = userService.login(user, loginRequest.password());

        Cookie accessToken = cookieUtils.makeCookie(
            ACCESS_TOKEN_COOKIE_NAME, tokenDTO.accessToken()
        );
        response.addCookie(accessToken);

        return ResponseEntity
            .ok(ResponseDTO.ok());
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletResponse response) {
        Cookie expiredCookie = cookieUtils.expireCookie(ACCESS_TOKEN_COOKIE_NAME);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteUser(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        userService.deleteUser(userId);

        Cookie expiredCookie = cookieUtils.expireCookie(ACCESS_TOKEN_COOKIE_NAME);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(ResponseDTO.ok());
    }

}
