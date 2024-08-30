package com.productservice.product.dto.response;

import java.util.List;
import lombok.Builder;


@Builder
public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    int price,
    String titleImage,
    List<ProductImageResponseDTO> images

) {

}
