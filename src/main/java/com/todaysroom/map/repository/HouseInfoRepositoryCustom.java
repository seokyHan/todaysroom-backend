package com.todaysroom.map.repository;

import com.todaysroom.map.entity.HouseInfo;

import java.util.List;

public interface HouseInfoRepositoryCustom {
    List<HouseInfo> findHouseInfoListByGuGun(String locationOfAgency);
    List<HouseInfo> findHouseInfoListByDong(String locationOfAgency, String legal);
    List<HouseInfo> findHouseInfoListByDongSearch(String dongName);
    List<HouseInfo> findLikedHouseInfoList(List<String> aptCode);
}
