package com.productservice.product.dto.response;

import lombok.Builder;

@Builder
public record OptionDetailResponseDTO(
    Long id,
    String type,
    boolean availability,
    int stock
) {

}
