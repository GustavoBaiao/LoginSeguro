package com.loginseguro.service;

import com.loginseguro.domain.entity.UserEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface IJwtService {

    String generateToken(UserEntity userEntity);

    Jwt validateToken(String token);
}
