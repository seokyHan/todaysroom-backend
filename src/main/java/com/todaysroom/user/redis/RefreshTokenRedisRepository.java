package com.todaysroom.user.redis;

import com.todaysroom.user.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    public Optional<RefreshToken> findByEmail(String email);
}
