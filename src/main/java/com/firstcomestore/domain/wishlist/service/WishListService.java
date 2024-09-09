package com.firstcomestore.domain.wishlist.service;

import com.firstcomestore.domain.wishlist.dto.request.AddWishListRequestDTO;
import com.firstcomestore.domain.wishlist.dto.response.WishListResponseDTO;
import java.util.List;

public interface WishListService {

    void addToWishList(Long userId, Long optionId, AddWishListRequestDTO requestDTO);

    List<WishListResponseDTO> getWishList(Long userId);

    void removeFromWishList(Long userId, Long wishId);

    void updateWishQuantity(Long userId, Long wishId, AddWishListRequestDTO requestDTO);
}
