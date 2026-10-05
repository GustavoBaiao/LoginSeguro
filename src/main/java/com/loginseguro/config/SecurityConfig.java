package com.loginseguro.config;

import com.loginseguro.security.CustomUserDetailsService;
import com.loginseguro.security.CustomUserDetails;
import com.loginseguro.domain.enums.RoleEnum;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/css/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users")
                        .hasAnyRole(RoleEnum.MANAGER.name(), RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/users/{id}").access(this::authorizeUserAccess)
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    private AuthorizationDecision authorizeUserAccess(
            Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
        var currentUser = authentication.get();
        if (currentUser == null || !currentUser.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        var canReadAllUsers = currentUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + RoleEnum.MANAGER.name())
                        || authority.getAuthority().equals("ROLE_" + RoleEnum.ADMIN.name()));
        var isOwnProfile = currentUser.getPrincipal() instanceof CustomUserDetails userDetails
                && userDetails.getId().toString().equalsIgnoreCase(context.getVariables().get("id"));

        return new AuthorizationDecision(canReadAllUsers || isOwnProfile);
    }
}
