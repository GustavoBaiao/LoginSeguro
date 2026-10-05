package com.loginseguro.mapper;

import com.loginseguro.domain.entity.SessionEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.oauth2.jwt.Jwt;

@Mapper(componentModel = "spring", imports = {UUID.class, LocalDateTime.class, ZoneOffset.class})
public interface ISessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(UUID.fromString(jwt.getSubject()))")
    @Mapping(target = "tokenId", source = "id")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.ofInstant(jwt.getIssuedAt(), ZoneOffset.UTC))")
    @Mapping(target = "expiresAt", expression = "java(LocalDateTime.ofInstant(jwt.getExpiresAt(), ZoneOffset.UTC))")
    @Mapping(target = "active", constant = "true")
    SessionEntity toEntity(Jwt jwt);
}
