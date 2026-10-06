package com.loginseguro.dto.response;

import com.loginseguro.domain.enums.RoleEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(

        UUID id,

        String name,

        String email,

        RoleEnum role,

        boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
