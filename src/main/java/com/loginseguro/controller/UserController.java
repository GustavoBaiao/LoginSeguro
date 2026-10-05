package com.loginseguro.controller;

import com.loginseguro.dto.request.UserRequestDTO;
import com.loginseguro.dto.request.UpdateUserRoleRequestDTO;
import com.loginseguro.dto.response.UserResponseDTO;
import com.loginseguro.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO requestDTO) {
        var user = userService.create(requestDTO);
        var location = URI.create("/users/" + user.id());
        return ResponseEntity.created(location).body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findCurrentUser() {
        var user = userService.findCurrentUser();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id,
                                          @RequestBody @Valid UpdateUserRoleRequestDTO requestDTO) {
        userService.updateRole(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAll(Pageable pageable) {
        var users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }
}
