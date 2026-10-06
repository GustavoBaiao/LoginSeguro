package com.loginseguro.repository;

import com.loginseguro.domain.entity.SessionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISessionRepository extends MongoRepository<SessionEntity, UUID> {

    Optional<SessionEntity> findByTokenId(String tokenId);
}
