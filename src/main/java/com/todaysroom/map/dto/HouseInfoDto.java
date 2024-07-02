package com.todaysroom.map.dto;

import com.todaysroom.map.entity.HouseInfo;

import static com.todaysroom.global.util.Utils.convertPrice;
import static com.todaysroom.global.util.Utils.getImage;

public record HouseInfoDto(int localCode,
                           String aptCode,
                           double exclusiveArea,
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
                           String aptName,
                           String image,
                           boolean likedStatus
                           ) {

    public static HouseInfoDto of(HouseInfo houseInfo, boolean likedStatus) {
        return new HouseInfoDto(
                houseInfo.getLocalCode(),
                houseInfo.getAptCode(),
                houseInfo.getExclusiveArea(),
                houseInfo.getBuildYear(),
                houseInfo.getLegal(),
                houseInfo.getRoadName(),
                houseInfo.getFloor(),
                houseInfo.getYear(),
                houseInfo.getMonth(),
                houseInfo.getDay(),
                houseInfo.getLng(),
                houseInfo.getLat(),
                houseInfo.getAmount(),
                houseInfo.getLocationOfAgency(),
                houseInfo.getAptName(),
                getImage(),
                likedStatus
        );
    }
    public static HouseInfoDto from (HouseInfo houseInfo){
        return new HouseInfoDto(houseInfo.getLocalCode(),
                houseInfo.getAptCode(),
                houseInfo.getExclusiveArea(),
                houseInfo.getBuildYear(),
                houseInfo.getLegal(),
                houseInfo.getRoadName(),
                houseInfo.getFloor(),
                houseInfo.getYear(),
                houseInfo.getMonth(),
                houseInfo.getDay(),
                houseInfo.getLng(),
                houseInfo.getLat(),
                convertPrice(houseInfo.getAmount()),
                houseInfo.getLocationOfAgency(),
                houseInfo.getAptName(),
                getImage(),
                false);
    }

    public HouseInfo toHouseInfoEntity(){
        return HouseInfo.builder()
                .aptCode(aptCode)
                .localCode(localCode)
                .exclusiveArea(exclusiveArea)
                .buildYear(buildYear)
                .legal(legal)
                .roadName(roadName)
                .floor(floor)
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
