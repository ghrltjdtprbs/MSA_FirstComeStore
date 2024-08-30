package com.userservice.user.dto.response;

import lombok.Builder;

@Builder
public record UserResponseDTO(
    String email,
    String name,
    String phone,
    String address
) {

}
