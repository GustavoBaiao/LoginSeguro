package com.loginseguro.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class SessionEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID userId;

    private String tokenId;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Boolean active;
}
