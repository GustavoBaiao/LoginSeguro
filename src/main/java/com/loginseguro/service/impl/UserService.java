package com.loginseguro.service.impl;

import com.loginseguro.domain.entity.UserEntity;
import com.loginseguro.domain.enums.RoleEnum;
import com.loginseguro.dto.request.UpdateUserRoleRequestDTO;
import com.loginseguro.exception.OperationNotAllowedException;
import java.time.LocalDateTime;
import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.response.UserResponseDTO;
import com.loginseguro.exception.EmailAlreadyExistsException;
import com.loginseguro.exception.UserNotFoundException;
import com.loginseguro.mapper.IUserMapper;
import com.loginseguro.repository.IUserRepository;
import com.loginseguro.service.IUserService;
import com.loginseguro.service.ICurrentActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
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
        var user = saveNewUser(userEntity);
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

    @Override
    public void activate(UUID id) {
        var user = findUserByIdOrThrow(id);
        user.setActive(true);
        saveUpdatedUser(user);
    }

    @Override
    public void deactivate(UUID id) {
        var user = findUserByIdOrThrow(id);
        validateNotOwnAccount(user.getId());
        user.setActive(false);
        saveUpdatedUser(user);
    }

    @Override
    public void updateRole(UUID id, UpdateUserRoleRequestDTO requestDTO) {
        var user = findUserByIdOrThrow(id);
        validateRoleChange(user.getId(), requestDTO.role());
        user.setRole(requestDTO.role());
        saveUpdatedUser(user);
    }

    private void validateNotOwnAccount(UUID userId) {
        if (userId.equals(currentActorService.getCurrentUserId())) {
            throw new OperationNotAllowedException("Você não pode desativar a própria conta");
        }
    }

    private void validateRoleChange(UUID userId, RoleEnum role) {
        if (userId.equals(currentActorService.getCurrentUserId()) && role != RoleEnum.ADMIN) {
            throw new OperationNotAllowedException("Você não pode reduzir o próprio perfil administrativo");
        }
    }

    private void saveUpdatedUser(UserEntity user) {
        user.setUpdatedAt(LocalDateTime.now());
        save(user);
    }

    private UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    private UserEntity saveNewUser(UserEntity userEntity) {
        try {
            return userRepository.insert(userEntity);
        } catch (DuplicateKeyException exception) {
            if (userRepository.existsByEmail(userEntity.getEmail())) {
                throw new EmailAlreadyExistsException();
            }
            throw exception;
        }
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
