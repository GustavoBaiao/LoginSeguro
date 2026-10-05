package com.loginseguro.domain.entity;

import com.loginseguro.domain.enums.RoleEnum;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private String name;

    private String email;

    private String password;

    private RoleEnum role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
