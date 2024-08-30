package com.productservice.product.dto.response;

import lombok.Builder;

@Builder
public record OptionResponseDTO(
    Long id,
    String type,
    int stock
) {

}
