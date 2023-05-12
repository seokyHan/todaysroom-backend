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
    @Indexed
    private String email;
    private String token;

    @Builder
    public RefreshToken(String email, String token){
        this.email = email;
        this.token = token;
    }

    public void changeToken(String token) {
        this.token = token;
    }
}
