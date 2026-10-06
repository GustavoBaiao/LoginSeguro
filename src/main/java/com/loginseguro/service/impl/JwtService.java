package com.loginseguro.service.impl;

import com.loginseguro.domain.entity.UserEntity;
import com.loginseguro.service.IJwtService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements IJwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long expiration;
    private final String issuer;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder,
                      @Value("${spring.jwt.expiration}") long expiration,
                      @Value("${spring.jwt.issuer}") String issuer) {
        if (expiration <= 0) {
            throw new IllegalArgumentException("JWT_EXPIRATION deve ser maior que zero, em segundos");
        }
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expiration = expiration;
        this.issuer = issuer;
    }

    @Override
    public String generateToken(UserEntity userEntity) {
        var claims = buildClaims(userEntity);
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Override
    public Jwt validateToken(String token) {
        var jwt = jwtDecoder.decode(token);
        validateRequiredClaims(jwt);
        return jwt;
    }

    private JwtClaimsSet buildClaims(UserEntity userEntity) {
        var issuedAt = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userEntity.getId().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expiration))
                .claim("email", userEntity.getEmail())
                .claim("role", userEntity.getRole().name())
                .build();
    }

    private void validateRequiredClaims(Jwt jwt) {
        if (jwt.getSubject() == null || jwt.getId() == null || jwt.getIssuedAt() == null
                || jwt.getExpiresAt() == null || !jwt.getExpiresAt().isAfter(Instant.now())) {
            throw new BadJwtException("Token inválido ou expirado");
        }
        try {
            UUID.fromString(jwt.getSubject());
            UUID.fromString(jwt.getId());
        } catch (IllegalArgumentException exception) {
            throw new BadJwtException("Identificação do token inválida");
        }
    }
}
