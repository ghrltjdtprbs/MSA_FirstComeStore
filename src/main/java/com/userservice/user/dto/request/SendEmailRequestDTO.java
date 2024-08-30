package com.userservice.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailRequestDTO(
    @Email(message = "유효한 이메일 주소여야 합니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    String email
) {

}
