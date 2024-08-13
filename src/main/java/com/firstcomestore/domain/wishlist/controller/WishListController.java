package com.firstcomestore.domain.wishlist.controller;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.domain.wishlist.dto.request.AddWishListRequestDTO;
import com.firstcomestore.domain.wishlist.dto.response.WishListResponseDTO;
import com.firstcomestore.domain.wishlist.service.WishListService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/{optionId}")
    public ResponseEntity<ResponseDTO<Void>> addToWishList(
        @PathVariable Long optionId,
        @Valid @RequestBody AddWishListRequestDTO requestDTO) {
        Long userId = getCurrentUserId();
        wishService.addToWishList(userId, optionId, requestDTO);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<WishListResponseDTO>>> getWishList() {
        Long userId = getCurrentUserId();
        List<WishListResponseDTO> response = wishService.getWishList(userId);
        return ResponseEntity.ok(ResponseDTO.okWithData(response));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<ResponseDTO<Void>> removeFromWishList(@PathVariable Long wishId) {
        Long userId = getCurrentUserId();
        wishService.removeFromWishList(userId, wishId);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    @PutMapping("/{wishId}")
    public ResponseEntity<ResponseDTO<Void>> updateWishQuantity(
        @PathVariable Long wishId,
        @Valid @RequestBody AddWishListRequestDTO requestDTO) {
        Long userId = getCurrentUserId();
        wishService.updateWishQuantity(userId, wishId, requestDTO);
        return ResponseEntity.ok(ResponseDTO.ok());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
