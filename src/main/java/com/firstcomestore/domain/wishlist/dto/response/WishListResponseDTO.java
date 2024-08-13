package com.firstcomestore.domain.wishlist.dto.response;

import lombok.Builder;

@Builder
public record WishListResponseDTO(
    Long id,
    int quantity,
    Long optionId,
    String optionType,
    boolean availability,
    int stock,
    Long productId,
    String productName
) {

}
