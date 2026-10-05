package com.loginseguro.security;

import com.loginseguro.repository.IUserRepository;
import com.loginseguro.service.ISessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtCookieManager jwtCookieManager;
    private final ISessionService sessionService;
    private final IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var token = jwtCookieManager.readToken(request);
        if (token.isPresent()) {
            try {
                authenticate(token.get(), request);
            } catch (JwtException | AuthenticationException exception) {
                SecurityContextHolder.clearContext();
                jwtCookieManager.removeCookie(response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token, HttpServletRequest request) {
        var session = sessionService.validate(token);
        var user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Autenticação inválida"));
        if (!user.isActive() || user.getRole() == null) {
            throw new BadCredentialsException("Autenticação inválida");
        }

        var userDetails = new CustomUserDetails(user);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
