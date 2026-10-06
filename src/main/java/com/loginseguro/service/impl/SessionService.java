package com.loginseguro.service.impl;

import com.loginseguro.domain.entity.SessionEntity;
import com.loginseguro.mapper.ISessionMapper;
import com.loginseguro.repository.ISessionRepository;
import com.loginseguro.service.IJwtService;
import com.loginseguro.service.ISessionService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService implements ISessionService {

    private final ISessionRepository sessionRepository;
    private final ISessionMapper sessionMapper;
    private final IJwtService jwtService;

    @Override
    public SessionEntity create(String token) {
        var jwt = jwtService.validateToken(token);
        var session = toEntity(jwt);
        return save(session);
    }

    @Override
    public SessionEntity validate(String token) {
        var jwt = jwtService.validateToken(token);
        var session = findSessionByTokenIdOrThrow(jwt.getId());
        validateActiveSession(session);
        validateExpiration(session);
        validateSessionOwner(session, jwt);
        return session;
    }

    @Override
    public void invalidate(String tokenId) {
        sessionRepository.findByTokenId(tokenId)
                .filter(session -> Boolean.TRUE.equals(session.getActive()))
                .ifPresent(this::deactivateSession);
    }

    private SessionEntity toEntity(Jwt jwt) {
        return sessionMapper.toEntity(jwt);
    }

    private SessionEntity findSessionByTokenIdOrThrow(String tokenId) {
        return sessionRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new BadCredentialsException("Sessão inválida ou expirada"));
    }

    private void validateActiveSession(SessionEntity session) {
        if (!Boolean.TRUE.equals(session.getActive())) {
            throw new BadCredentialsException("Sessão inválida ou expirada");
        }
    }

    private void validateExpiration(SessionEntity session) {
        if (session.getExpiresAt() == null
                || !session.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new BadCredentialsException("Sessão inválida ou expirada");
        }
    }

    private void validateSessionOwner(SessionEntity session, Jwt jwt) {
        if (!UUID.fromString(jwt.getSubject()).equals(session.getUserId())) {
            throw new BadCredentialsException("Sessão inválida ou expirada");
        }
    }

    private void deactivateSession(SessionEntity session) {
        session.setActive(false);
        save(session);
    }

    private SessionEntity save(SessionEntity session) {
        return sessionRepository.save(session);
    }
}
