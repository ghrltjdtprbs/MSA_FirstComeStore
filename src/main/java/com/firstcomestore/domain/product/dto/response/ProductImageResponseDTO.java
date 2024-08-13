package com.firstcomestore.domain.product.dto.response;

import lombok.Builder;

@Builder
public record ProductImageResponseDTO(
    Long id,
    String url
) {

}
