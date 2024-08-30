package com.productservice.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOptionRequestDTO(
    @NotBlank String type,
    @Min(value = 1, message = "재고는 최소 1 이상이어야 합니다.") int stock

) {

}
