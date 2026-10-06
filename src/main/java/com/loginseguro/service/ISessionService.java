package com.loginseguro.service;

import com.loginseguro.domain.entity.SessionEntity;

public interface ISessionService {

    SessionEntity create(String token);

    SessionEntity validate(String token);

    void invalidate(String tokenId);
}
