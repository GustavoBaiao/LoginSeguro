package com.loginseguro.controller.auth;

import com.loginseguro.dto.request.auth.LoginRequestDTO;
import com.loginseguro.dto.response.auth.LoginResponseDTO;
import com.loginseguro.security.JwtCookieManager;
import com.loginseguro.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtCookieManager jwtCookieManager;

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> csrf(CsrfToken csrfToken) {

        return ResponseEntity.ok(csrfToken);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        var token = authService.login(loginRequestDTO);
        jwtCookieManager.addCookie(token, response);
        return ResponseEntity.ok(new LoginResponseDTO("Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        var token = jwtCookieManager.readToken(request).orElse(null);
        authService.logout(token);
        jwtCookieManager.removeCookie(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

}
