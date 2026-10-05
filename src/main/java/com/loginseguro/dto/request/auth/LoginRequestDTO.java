package com.loginseguro.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotBlank(message = "O email não pode ser vazio")
        @Email(message = "O email deve ser válido")
        @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "A senha não pode ser vazia")
        String password
) {
}
