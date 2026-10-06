package com.loginseguro.security;

import com.loginseguro.service.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieManager {

    private static final String COOKIE_NAME = "loginseguro_token";

    private final IJwtService jwtService;
    private final boolean secure;

    public JwtCookieManager(IJwtService jwtService, @Value("${spring.jwt.cookie.secure}") boolean secure) {
        this.jwtService = jwtService;
        this.secure = secure;
    }

    public void addCookie(String token, HttpServletResponse response) {
        var jwt = jwtService.validateToken(token);
        var maxAge = Duration.between(Instant.now(), jwt.getExpiresAt()).getSeconds();
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(token, Math.max(0, maxAge)).toString());
    }

    public void removeCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("", 0).toString());
    }

    public Optional<String> readToken(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(cookie -> cookie.getValue())
                .filter(token -> token != null && !token.isBlank())
                .findFirst();
    }

    private ResponseCookie buildCookie(String value, long maxAge) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
