package com.loginseguro.controller.auth;

import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.request.auth.LoginRequestDTO;
import com.loginseguro.exception.EmailAlreadyExistsException;
import com.loginseguro.security.CustomUserDetails;
import com.loginseguro.security.JwtCookieManager;
import com.loginseguro.service.IAuthService;
import com.loginseguro.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final IAuthService authService;
    private final IUserService userService;
    private final JwtCookieManager jwtCookieManager;

    @GetMapping("/login")
    public String showLogin(Model model, Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/bem-vindo";
        }
        model.addAttribute("loginRequestDTO", new LoginRequestDTO("", ""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequestDTO") LoginRequestDTO requestDTO,
                        BindingResult bindingResult, Model model, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            var token = authService.login(requestDTO);
            jwtCookieManager.addCookie(token, response);
        } catch (AuthenticationException exception) {
            model.addAttribute("loginError", "Email ou senha inválidos");
            return "auth/login";
        }
        return "redirect:/bem-vindo";
    }

    @GetMapping("/register")
    public String showRegister(Model model, Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/bem-vindo";
        }
        model.addAttribute("userRequestDTO", new UserRequestDTO("", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userRequestDTO") UserRequestDTO requestDTO,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.create(requestDTO);
        } catch (EmailAlreadyExistsException exception) {
            bindingResult.rejectValue("email", "email.alreadyExists", exception.getMessage());
            return "auth/register";
        }
        return "redirect:/login?registered";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(jwtCookieManager.readToken(request).orElse(null));
        jwtCookieManager.removeCookie(response);
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
}
