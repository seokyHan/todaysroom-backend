package com.todaysroom.user.redis;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlackListRepository extends CrudRepository<BlackList, String> {

    BlackList findByToken(String token);
}
