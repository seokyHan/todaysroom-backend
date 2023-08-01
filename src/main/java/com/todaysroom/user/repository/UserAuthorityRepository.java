package com.todaysroom.user.repository;

import com.todaysroom.user.entity.UserAuthority;
import com.todaysroom.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    UserAuthority findByUserEntity(UserEntity userEntity);

}
