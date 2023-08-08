package com.todaysroom.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-userAuthority")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    @JsonBackReference(value = "auth-userAuthority")
    private Authority auth;

    @Builder
    public UserAuthority (Long id, UserEntity userEntity, Authority auth){
        this.id = id;
        this.userEntity = userEntity;
        this.auth = auth;
    }

    public void authUpdate(Authority auth){
        this.auth = auth;
    }

    
}
