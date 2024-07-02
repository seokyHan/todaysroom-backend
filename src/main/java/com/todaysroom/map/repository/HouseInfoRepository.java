package com.todaysroom.map.repository;

import com.todaysroom.map.entity.HouseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HouseInfoRepository extends JpaRepository<HouseInfo, Long>, HouseInfoRepositoryCustom {
    @Query(value = "SELECT * FROM HOUSE_INFO WHERE LEGAL=:dongName ORDER BY RAND() ASC LIMIT 4", nativeQuery = true)
    List<HouseInfo> recommendHouseInfoByDongName(@Param("dongName") String dongName);

    @Query(value = "SELECT * FROM HOUSE_INFO WHERE LOCATION_OF_AGENCY LIKE '서울%' ORDER BY RAND() ASC LIMIT 4", nativeQuery = true)
    List<HouseInfo> recommendHouseInfo();
}
