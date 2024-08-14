package com.firstcomestore.domain.user.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.util.CookieUtils;
import com.firstcomestore.domain.user.dto.request.UpdatePasswordRequestDTO;
import com.firstcomestore.domain.user.dto.request.UpdateUserRequestDTO;
import com.firstcomestore.domain.user.dto.response.UserResponseDTO;
import com.firstcomestore.domain.user.service.MyPageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final CookieUtils cookieUtils;
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";


    @GetMapping
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserDetails() throws Exception {
        Long userId = getCurrentUserId();
        UserResponseDTO userResponseDTO = myPageService.getUserDetails(userId);
        return ResponseEntity.ok(ResponseDTO.okWithData(userResponseDTO));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO<Void>> updateUserDetails(
        @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) throws Exception {
        Long userId = getCurrentUserId();
        myPageService.updateUserDetails(userId, updateUserRequestDTO);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDTO<Void>> updatePassword(
        @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
        HttpServletResponse response) {

        Long userId = getCurrentUserId();
        myPageService.updatePassword(userId, updatePasswordRequestDTO);
        expireAccessTokenCookie(response);

        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private void expireAccessTokenCookie(HttpServletResponse response) {
        Cookie expiredCookie = cookieUtils.expireCookie(ACCESS_TOKEN_COOKIE_NAME);
        response.addCookie(expiredCookie);
    }
}
