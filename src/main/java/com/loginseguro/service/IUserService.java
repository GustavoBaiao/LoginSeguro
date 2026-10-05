package com.loginseguro.service;

import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.request.UpdateUserRoleRequestDTO;
import com.loginseguro.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    UserResponseDTO create(@NotNull @Valid UserRequestDTO userRequestDTO);

    UserResponseDTO findById(UUID id);

    UserResponseDTO findCurrentUser();

    UserResponseDTO findByEmail(String email);

    Page<UserResponseDTO> findAll(Pageable pageable);

    void activate(UUID id);

    void deactivate(UUID id);

    void updateRole(UUID id, @NotNull @Valid UpdateUserRoleRequestDTO requestDTO);
}
