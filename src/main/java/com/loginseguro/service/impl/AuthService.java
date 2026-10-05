package com.loginseguro.service.impl;

import com.loginseguro.domain.entity.UserEntity;
import com.loginseguro.dto.request.auth.LoginRequestDTO;
import com.loginseguro.repository.IUserRepository;
import com.loginseguro.security.CustomUserDetails;
import com.loginseguro.service.IAuthService;
import com.loginseguro.service.IJwtService;
import com.loginseguro.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository userRepository;
    private final IJwtService jwtService;
    private final ISessionService sessionService;

    @Override
    public String login(LoginRequestDTO loginRequestDTO) {
        var authentication = authenticate(loginRequestDTO);
        var user = findAuthenticatedUser(authentication);
        var token = jwtService.generateToken(user);
        sessionService.create(token);
        return token;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        String tokenId;
        try {
            tokenId = jwtService.validateToken(token).getId();
        } catch (JwtException exception) {
            return;
        }
        sessionService.invalidate(tokenId);
    }

    private Authentication authenticate(LoginRequestDTO loginRequestDTO) {
        var credentials = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequestDTO.email().trim().toLowerCase(), loginRequestDTO.password());
        return authenticationManager.authenticate(credentials);
    }

    private UserEntity findAuthenticatedUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        var user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
        if (!user.isActive()) {
            throw new BadCredentialsException("Credenciais inválidas");
        }
        return user;
    }
}
