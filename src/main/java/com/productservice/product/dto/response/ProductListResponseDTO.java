package com.productservice.product.dto.response;

import lombok.Builder;

@Builder
public record ProductListResponseDTO(
    Long id,
    String name,
    int price,
    String titleImage
) {

}
