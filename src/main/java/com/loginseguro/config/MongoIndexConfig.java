package com.loginseguro.config;

import com.loginseguro.domain.entity.UserEntity;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfig {

    @Bean
    public ApplicationRunner createUserIndexes(MongoTemplate mongoTemplate) {
        return args -> mongoTemplate.indexOps(UserEntity.class).createIndex(
                new Index().on("email", Sort.Direction.ASC).unique().named("email_unique"));
    }
}
