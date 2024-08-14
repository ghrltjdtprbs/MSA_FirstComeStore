package com.firstcomestore.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderRequestDTO(
    @NotBlank(message = "배송 주소는 필수 항목입니다.")
    String deliveryAddress,

    @NotBlank(message = "배송 연락처는 필수 항목 입니다.")
    String deliveryContact
) {

}
