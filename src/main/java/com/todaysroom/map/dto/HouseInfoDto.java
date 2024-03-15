package com.todaysroom.map.dto;

import com.todaysroom.file.entity.UserFiles;
import com.todaysroom.map.entity.HouseInfo;

public record HouseInfoDto(double exclusiveArea,
                           String buildYear,
                           String legal,
                           String roadName,
                           int floor,
                           int year,
                           int month,
                           int day,
                           double lng,
                           double lat,
                           String amount,
                           String locationOfAgency,
                           String aptName
                           ) {

    public HouseInfo toHouseInfoEntity(){
        return HouseInfo.builder()
                .exclusiveArea(exclusiveArea)
                .buildYear(buildYear)
                .legal(legal)
                .roadName(roadName)
                .year(year)
                .month(month)
                .day(day)
                .lng(lng)
                .lat(lat)
                .amount(amount)
                .locationOfAgency(locationOfAgency)
                .aptName(aptName)
                .build();
    }

}
