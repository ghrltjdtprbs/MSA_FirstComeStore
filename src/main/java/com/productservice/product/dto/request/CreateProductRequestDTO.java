package com.productservice.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateProductRequestDTO(
    @NotBlank String name,
    @NotBlank String description,
    @Min(0) int price,
    @NotBlank String titleImage,
    List<ProductImageRequestDTO> images
) {

}
