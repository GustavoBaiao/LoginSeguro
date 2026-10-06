package com.loginseguro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

        @NotBlank(message = "O nome do usuário não pode ser vazio")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name,

        @NotBlank(message = "O email do usuário não pode ser vazio")
        @Email(message = "O email do usuário deve ser válido")
        @Size(max = 255, message = "O email deve ter no máximo 255 caracteres")
        String email,

        @NotBlank(message = "A senha não pode ser vazia")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "A senha deve possuir letra maiúscula, letra minúscula, número e caractere especial")
        String password
) {
}
