package com.todaysroom.likeHouse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.todaysroom.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "LikeHouse")
public class LikeHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-liked")
    private UserEntity user;

    @Column(name = "apt_code")
    private String aptCode;

    @Builder
    public LikeHouse(Long id, UserEntity user, String aptCode) {
        this.id = id;
        this.user = user;
        this.aptCode = aptCode;
    }
}
