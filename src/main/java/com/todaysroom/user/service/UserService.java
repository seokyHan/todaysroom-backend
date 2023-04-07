package com.todaysroom.user.service;

import com.todaysroom.user.dto.UserInfoDto;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    //refactoring 필요 필요 데이터만 가져올 수 있도록 커스터마이징
    @Transactional
    public UserInfoDto getUserInfo(String email){
        UserEntity userEntity = userRepository.findUserByEmail(email);

        return UserInfoDto.from(userEntity);
    }

}
