package com.userservice.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @Email(message = "유효한 이메일 주소여야 합니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    String email,
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password
) {

}
