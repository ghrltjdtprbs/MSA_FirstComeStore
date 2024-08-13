package com.firstcomestore.domain.wishlist.dto.request;

import jakarta.validation.constraints.Min;

public record AddWishListRequestDTO(

    @Min(1)
    int quantity
) {

}
