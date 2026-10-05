package com.loginseguro.controller;

import com.loginseguro.domain.enums.RoleEnum;
import com.loginseguro.dto.request.UpdateUserRoleRequestDTO;
import com.loginseguro.service.IUserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserPageController {
    private final IUserService userService;

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.findCurrentUser());
        return "user/profile";
    }

    @GetMapping("/users")
    public String list(@PageableDefault(size = 10, sort = "name") Pageable pageable, Model model) {
        model.addAttribute("users", userService.findAll(pageable));
        return "management/users";
    }

    @GetMapping("/users/{id}")
    public String details(@PathVariable UUID id, Model model) {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", RoleEnum.values());
        if (!model.containsAttribute("updateUserRoleRequestDTO")) {
            model.addAttribute("updateUserRoleRequestDTO", new UpdateUserRoleRequestDTO(user.role()));
        }
        return "management/user-details";
    }

    @PostMapping("/users/{id}/activate")
    public String activate(@PathVariable UUID id, RedirectAttributes attributes) {
        userService.activate(id);
        return redirectToUser(id, "Usuário ativado", attributes);
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivate(@PathVariable UUID id, RedirectAttributes attributes) {
        userService.deactivate(id);
        return redirectToUser(id, "Usuário desativado", attributes);
    }

    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable UUID id,
            @Valid @ModelAttribute UpdateUserRoleRequestDTO updateUserRoleRequestDTO,
            BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return details(id, model);
        }
        userService.updateRole(id, updateUserRoleRequestDTO);
        return redirectToUser(id, "Perfil atualizado", attributes);
    }

    private String redirectToUser(UUID id, String message, RedirectAttributes attributes) {
        attributes.addFlashAttribute("successMessage", message);
        return "redirect:/users/" + id;
    }
}
