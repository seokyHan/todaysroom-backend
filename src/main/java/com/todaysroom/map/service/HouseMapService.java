package com.todaysroom.map.service;


import com.querydsl.core.util.StringUtils;
import com.todaysroom.batch.service.CronService;
import com.todaysroom.likeHouse.entity.LikeHouse;
import com.todaysroom.likeHouse.repository.LikeHouseRepository;
import com.todaysroom.map.dto.*;
import com.todaysroom.map.entity.Dong;
import com.todaysroom.map.entity.Gugun;
import com.todaysroom.map.entity.HouseInfo;
import com.todaysroom.map.repository.DongRepository;
import com.todaysroom.map.repository.GugunRepository;
import com.todaysroom.map.repository.HouseInfoRepository;
import com.todaysroom.map.repository.SidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class HouseMapService {

    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;
    private final DongRepository dongRepository;
    private final HouseInfoRepository houseInfoRepository;
    private final LikeHouseRepository likeHouseRepository;

    public List<SidoDto> getSidoList(){
        return sidoRepository.findAll().stream().map(SidoDto::from).collect(Collectors.toList());
    }

    public List<GuGunDto> getGugunList(String sidoCode){
        List<Gugun> gugunList = gugunRepository.findBySidoCode(sidoCode);
        return gugunList.stream().map(GuGunDto::from).collect(Collectors.toList());
    }

    public List<DongDto> getDongList(MapRequest mapRequest) {
        List<Dong> dongList = dongRepository.findDongList(mapRequest.sidoName(), mapRequest.gugunName());
        return dongList.stream().map(DongDto::from).collect(Collectors.toList());
    }

    public List<HouseInfoDto> getHouseInfoListByGuGun(MapRequest mapRequest){
        String locationOfAgency = String.join(" ",  mapRequest.sidoName(), mapRequest.gugunName());
        List<HouseInfo> houseInfoList = houseInfoRepository.findHouseInfoListByGuGun(locationOfAgency);
        return houseInfoList.stream().map(HouseInfoDto::from).collect(Collectors.toList());
    }

    public List<HouseInfoDto> getHouseInfoListByDong(MapRequest mapRequest){
        String locationOfAgency = String.join(" ",  mapRequest.sidoName(), mapRequest.gugunName());
        List<HouseInfo> houseInfoList = houseInfoRepository.findHouseInfoListByDong(locationOfAgency, mapRequest.dongName());
        return houseInfoList.stream().map(HouseInfoDto::from).collect(Collectors.toList());
    }

    public List<HouseInfoDto> getHouseInfoListByDongSearch(String dongName){
        List<HouseInfo> houseInfoList = houseInfoRepository.findHouseInfoListByDongSearch(dongName);
        return houseInfoList.stream().map(HouseInfoDto::from).collect(Collectors.toList());
    }

    public List<HouseInfoDto> getHouseInfoRecommend(String dongName){
        List<HouseInfo> houseInfoList = StringUtils.isNullOrEmpty(dongName) ?
                houseInfoRepository.recommendHouseInfo() :
                houseInfoRepository.recommendHouseInfoByDongName(dongName);
        return houseInfoList.stream().map(HouseInfoDto::from).collect(Collectors.toList());
    }

    public List<HouseInfo> getLikedHouseInfoList(List<String> aptCode){
        return houseInfoRepository.findLikedHouseInfoList(aptCode);
    }

    public List<HouseInfoDto> getUserLikedHouseListByGuGun(MapRequest mapRequest, Long userId){
        Map<String, Boolean> likedStatusMap = setLikedStatus(userId);
        String locationOfAgency = String.join(" ",  mapRequest.sidoName(), mapRequest.gugunName());
        List<HouseInfoDto> houseInfoList = houseInfoRepository.findHouseInfoListByGuGun(locationOfAgency)
                .stream()
                .map(houseInfo -> HouseInfoDto.of(houseInfo, likedStatusMap.containsKey(houseInfo.getAptCode())))
                .collect(Collectors.toList());

        return houseInfoList;
    }

    public List<HouseInfoDto> getUserLikedHouseListByDong(MapRequest mapRequest, Long userId){
        Map<String, Boolean> likedStatusMap = setLikedStatus(userId);
        String locationOfAgency = String.join(" ",  mapRequest.sidoName(), mapRequest.gugunName());
        List<HouseInfoDto> houseInfoList = houseInfoRepository.findHouseInfoListByDong(locationOfAgency, mapRequest.dongName())
                .stream()
                .map(houseInfo -> HouseInfoDto.of(houseInfo, likedStatusMap.containsKey(houseInfo.getAptCode())))
                .collect(Collectors.toList());

        return houseInfoList;
    }

    public List<HouseInfoDto> getUserLikedHouseListByDongSearch(String dongName, Long userId){
        Map<String, Boolean> likedStatusMap = setLikedStatus(userId);
        List<HouseInfoDto> houseInfoList = houseInfoRepository.findHouseInfoListByDongSearch(dongName)
                .stream()
                .map(houseInfo -> HouseInfoDto.of(houseInfo, likedStatusMap.containsKey(houseInfo.getAptCode())))
                .collect(Collectors.toList());

        return houseInfoList;
    }

    private Map<String, Boolean> setLikedStatus(Long userId){
        List<LikeHouse> likeHouseList = likeHouseRepository.findByUserId(userId);

        return likeHouseList.stream()
                .collect(Collectors.toMap(l -> l.getAptCode(), l -> true, (oldValue, newValue) -> newValue, HashMap::new));
    }
}
