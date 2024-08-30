package com.productservice.product.dto.response;

import lombok.Builder;

@Builder
public record OptionDetailDTO(
    Long optionId,
    String optionType,
    boolean availability,
    int stock,
    Long productId,
    String productName
) {

}
