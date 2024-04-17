package com.todaysroom.likeHouse.service;

import com.todaysroom.global.exception.CustomException;
import com.todaysroom.likeHouse.dto.LikeHouseRequestDto;
import com.todaysroom.likeHouse.dto.LikeHouseResponseDto;
import com.todaysroom.likeHouse.entity.LikeHouse;
import com.todaysroom.likeHouse.repository.LikeHouseRepository;
import com.todaysroom.map.dto.HouseInfoDto;
import com.todaysroom.map.service.HouseMapService;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.todaysroom.global.exception.code.AuthResponseCode.USER_NOT_FOUND;
import static com.todaysroom.global.exception.code.CommonResponseCode.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeHouseService {

    private final LikeHouseRepository likeHouseRepository;
    private final HouseMapService houseMapService;
    private final UserRepository userRepository;

    public List<HouseInfoDto> getUserLikedHouseList(Long id){
        List<String> aptCodes = likeHouseRepository.findByUserId(id)
                .stream()
                .map(LikeHouse::getAptCode)
                .collect(Collectors.toList());

        return aptCodes.isEmpty() ? Collections.emptyList() : houseMapService.getLikedHouseInfoList(aptCodes)
                .stream()
                .map(houseInfo -> HouseInfoDto.of(houseInfo, true))
                .collect(Collectors.toList());
    }

    public List<LikeHouseResponseDto> getLikedAptCode(Long id){
        return likeHouseRepository.findByUserId(id).stream().map(LikeHouseResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public Long addLikedHouse(LikeHouseRequestDto likeHouseRequestDto){
        UserEntity user = userRepository.findById(likeHouseRequestDto.userId()).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "해당 User를 찾을 수 없습니다."));
        LikeHouse likeHouse  = LikeHouse.builder()
                .user(user)
                .aptCode(likeHouseRequestDto.aptCode())
                .build();

        return likeHouseRepository.save(likeHouse).getId();
    }

}
