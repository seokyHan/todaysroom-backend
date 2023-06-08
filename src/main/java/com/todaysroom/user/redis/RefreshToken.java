package com.todaysroom.user.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@RedisHash(value = "RefreshToken", timeToLive = 1209600)
public class RefreshToken {
    @Id
    @Indexed
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    @Indexed
    private String token;

    @Builder
    public RefreshToken(String email, String token, Collection<? extends GrantedAuthority> authorities){
        this.email = email;
        this.token = token;
        this.authorities = authorities;
    }



    public void changeToken(String token) {
        this.token = token;
    }
}
