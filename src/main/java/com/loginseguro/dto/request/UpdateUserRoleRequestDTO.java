package com.loginseguro.dto.request;

import com.loginseguro.domain.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequestDTO(
        @NotNull(message = "O perfil do usuário é obrigatório")
        RoleEnum role
) {
}
