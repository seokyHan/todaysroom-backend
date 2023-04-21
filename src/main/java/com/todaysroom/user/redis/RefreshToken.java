package com.todaysroom.user.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("RefreshToken")
public class RefreshToken {
    @Id
    private String id;

    private String token;

    @Builder
    public RefreshToken(String id, String token){
        this.id = id;
        this.token = token;
    }

    public void changeToken(String token) {
        this.token = token;
    }
}
