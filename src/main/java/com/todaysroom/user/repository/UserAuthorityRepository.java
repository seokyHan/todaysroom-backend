package com.todaysroom.user.repository;

import com.todaysroom.user.entity.Authority;
import com.todaysroom.user.entity.UserAuthority;
import com.todaysroom.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    String findUserAuthoritiesByUserEntity(UserEntity userEntity);
}
