package com.todaysroom.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authority_name", length = 30)
    private String authorityName;

    @OneToMany
    @JoinColumn(name = "authority_id", referencedColumnName = "id")
    @JsonBackReference(value = "auth-userAuthority")
    private Set<UserAuthority> authorities;


    @Builder
    public Authority(Long id, String authorityName, Set<UserAuthority> authorities){
        this.id = id;
        this.authorityName = authorityName;
        this.authorities = authorities;
    }
}
