package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;

public record TokenDto(Long id,
                       String token,
                       String name,
                       String userEmail,
                       String nickname,
                       String recentSearch) {

    public TokenDto(Long id, String token, String name, String userEmail, String nickname, String recentSearch) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.userEmail = userEmail;
        this.nickname = nickname;
        this.recentSearch = recentSearch;
    }
}
