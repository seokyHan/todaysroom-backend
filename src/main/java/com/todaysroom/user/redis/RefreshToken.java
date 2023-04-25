package com.todaysroom.user.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "RefreshToken", timeToLive = 1209600)
public class RefreshToken {
    @Id
    private String token;
    private Long id;

    @Builder
    public RefreshToken(String token, Long id){
        this.token = token;
        this.id = id;
    }

    public void changeToken(String token) {
        this.token = token;
    }
}
