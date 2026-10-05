package com.loginseguro.repository;

import com.loginseguro.domain.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserRepository extends MongoRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
