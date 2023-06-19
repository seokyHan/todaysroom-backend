package com.todaysroom.user.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "BlackList")
public class BlackList {
    @Id
    @Indexed
    private String token;

    @TimeToLive
    private Long expirationInSeconds;

    @Builder
    public BlackList(String token, Long expirationInSeconds) {
        this.token = token;
        this.expirationInSeconds = expirationInSeconds;
    }
}
