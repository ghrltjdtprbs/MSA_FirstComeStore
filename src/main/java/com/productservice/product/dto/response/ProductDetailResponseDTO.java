package com.productservice.product.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductDetailResponseDTO(
    Long id,
    String name,
    String description,
    String titleImage,
    List<ProductImageResponseDTO> productImages,
    List<OptionDetailResponseDTO> options
) {

}
