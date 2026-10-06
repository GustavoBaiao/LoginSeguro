package com.loginseguro.mapper;

import com.loginseguro.domain.entity.UserEntity;
import com.loginseguro.domain.enums.RoleEnum;
import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = RoleEnum.class)
public interface IUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(RoleEnum.USER)")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toResponseDTO(UserEntity userEntity);
}
