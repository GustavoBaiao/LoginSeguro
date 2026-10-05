package com.loginseguro.service.impl;

import com.loginseguro.domain.entity.UserEntity;
import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.response.UserResponseDTO;
import com.loginseguro.exception.EmailAlreadyExistsException;
import com.loginseguro.exception.UserNotFoundException;
import com.loginseguro.mapper.IUserMapper;
import com.loginseguro.repository.IUserRepository;
import com.loginseguro.service.IUserService;
import com.loginseguro.service.ICurrentActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ICurrentActorService currentActorService;

    @Override
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        var email = normalizeEmail(userRequestDTO.email());
        validateEmailAvailability(email);
        var userEntity = prepareUserForCreation(userRequestDTO, email);
        var user = save(userEntity);
        return toResponseDTO(user);
    }

    @Override
    public UserResponseDTO findById(UUID id) {
        var user = findUserByIdOrThrow(id);

        return toResponseDTO(user);
    }

    @Override
    public UserResponseDTO findCurrentUser() {
        var id = currentActorService.getCurrentUserId();
        var user = findUserByIdOrThrow(id);
        return toResponseDTO(user);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        var user = findUserByEmailOrThrow(normalizeEmail(email));

        return toResponseDTO(user);
    }

    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        var users = userRepository.findAll(pageable);
        return users.map(this::toResponseDTO);
    }

    private UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    private UserEntity toEntity(UserRequestDTO userRequestDTO) {
        return userMapper.toEntity(userRequestDTO);
    }

    private UserResponseDTO toResponseDTO(UserEntity userEntity) {
        return userMapper.toResponseDTO(userEntity);
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
    }

    private UserEntity prepareUserForCreation(UserRequestDTO userRequestDTO, String email) {
        var user = toEntity(userRequestDTO);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        return user;
    }

    private UserEntity findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    private UserEntity findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private String normalizeEmail(String email) {

        return email.trim().toLowerCase();
    }
}
