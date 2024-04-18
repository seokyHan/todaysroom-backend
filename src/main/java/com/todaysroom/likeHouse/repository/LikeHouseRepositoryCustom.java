package com.todaysroom.likeHouse.repository;

public interface LikeHouseRepositoryCustom {

    void deleteByUserIdAndAptCode(Long userId, String aptCode);
}
