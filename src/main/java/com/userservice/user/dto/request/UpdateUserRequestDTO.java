package com.userservice.user.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateUserRequestDTO(
    String name,
    String phone,
    @Email String email,
    String address
) {

}
