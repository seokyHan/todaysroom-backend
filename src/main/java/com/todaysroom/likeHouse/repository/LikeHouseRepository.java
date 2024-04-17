package com.todaysroom.likeHouse.repository;

import com.todaysroom.likeHouse.entity.LikeHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeHouseRepository extends JpaRepository<LikeHouse, Long> {

    List<LikeHouse> findByUserId(Long id);
    List<LikeHouse> findByUserIdAndAptCode(Long userId, String aptCode);
}
