package com.productservice.product.dto.response;

import lombok.Builder;

@Builder
public record ProductImageResponseDTO(
    Long id,
    String url
) {

}
