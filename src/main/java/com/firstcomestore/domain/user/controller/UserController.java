package com.firstcomestore.domain.user.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.util.CookieUtils;
import com.firstcomestore.domain.user.dto.request.CreateUserRequestDTO;
import com.firstcomestore.domain.user.dto.request.LoginRequestDTO;
import com.firstcomestore.domain.user.dto.request.TokenDTO;
import com.firstcomestore.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final CookieUtils cookieUtils;
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> signUp(
        @Valid @RequestBody CreateUserRequestDTO requestDTO) throws Exception {
        ResponseDTO<Object> response = userService.signUp(requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteUser(HttpServletResponse response) {
        Long userId = getCurrentUserId();
        userService.deleteUser(userId);
        expireAccessTokenCookie(response);

        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Void>> login(
        @RequestBody @Valid LoginRequestDTO loginRequest, HttpServletResponse response)
        throws Exception {
        TokenDTO tokenDTO = userService.login(loginRequest.email(), loginRequest.password());
        addAccessTokenToCookie(response, tokenDTO.accessToken());

        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(HttpServletResponse response) {
        expireAccessTokenCookie(response);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private void addAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = cookieUtils.makeCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        response.addCookie(accessTokenCookie);
    }

    private void expireAccessTokenCookie(HttpServletResponse response) {
        Cookie expiredCookie = cookieUtils.expireCookie(ACCESS_TOKEN_COOKIE_NAME);
        response.addCookie(expiredCookie);
    }
}
