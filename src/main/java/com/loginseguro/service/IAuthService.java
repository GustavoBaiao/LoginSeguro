package com.loginseguro.service;

import com.loginseguro.dto.request.auth.LoginRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface IAuthService {

    String login(@NotNull @Valid LoginRequestDTO loginRequestDTO);

    void logout(String token);
}
