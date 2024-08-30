package com.userservice.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(
    @Email(message = "유효한 이메일 주소여야 합니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 10자리 또는 11자리 숫자여야 합니다.")
    String phone,

    @NotBlank(message = "주소는 필수 항목입니다.")
    String address
) {

}
