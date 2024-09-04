package com.productservice.product.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateMaxPurchaseLimitRequestDTO(
    @Min(1)
    Integer maxPurchaseLimit
) {

}
