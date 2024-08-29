package com.firstcomestore.domain.wishlist.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.domain.wishlist.dto.request.AddWishListRequestDTO;
import com.firstcomestore.domain.wishlist.dto.response.WishListResponseDTO;
import com.firstcomestore.domain.wishlist.service.WishListService;
import com.firstcomestore.common.jwt.JwtCookieService;
import com.firstcomestore.domain.order.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishListController {

    private final WishListService wishService;
    private final JwtCookieService jwtCookieService;

    @PostMapping("/options/{optionId}")
    public ResponseEntity<ResponseDTO<Void>> addToWishList(
        @PathVariable Long optionId,
        @Valid @RequestBody AddWishListRequestDTO requestDTO,
        HttpServletRequest request) {

        Long userId = getUserIdOrThrow(request);
        wishService.addToWishList(userId, optionId, requestDTO);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<WishListResponseDTO>>> getWishList(
        HttpServletRequest request) {
        Long userId = getUserIdOrThrow(request);
        List<WishListResponseDTO> response = wishService.getWishList(userId);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<ResponseDTO<Void>> removeFromWishList(
        @PathVariable Long wishId,
        HttpServletRequest request) {

        Long userId = getUserIdOrThrow(request);
        wishService.removeFromWishList(userId, wishId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PutMapping("/{wishId}")
    public ResponseEntity<ResponseDTO<Void>> updateWishQuantity(
        @PathVariable Long wishId,
        @Valid @RequestBody AddWishListRequestDTO requestDTO,
        HttpServletRequest request) {

        Long userId = getUserIdOrThrow(request);
        wishService.updateWishQuantity(userId, wishId, requestDTO);
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
