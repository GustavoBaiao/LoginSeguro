package com.loginseguro.controller;

import com.loginseguro.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final IUserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/bem-vindo")
    public String welcome(Model model) {
        model.addAttribute("user", userService.findCurrentUser());
        return "user/welcome";
    }
}
