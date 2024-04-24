package com.todaysroom.map.repository;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todaysroom.map.entity.HouseInfo;
import com.todaysroom.map.entity.QHouseInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HouseInfoRepositoryImpl implements HouseInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HouseInfo> findHouseInfoListByGuGun(String locationOfAgency) {
        final QHouseInfo houseInfo = new QHouseInfo("houseInfo");
        return queryFactory.select(houseInfo)
                .from(houseInfo)
                .where(houseInfo.locationOfAgency.eq(locationOfAgency))
                .orderBy(houseInfo.locationOfAgency.asc())
                .fetch();
    }

    @Override
    public List<HouseInfo> findHouseInfoListByDong(String locationOfAgency, String legal) {
        final QHouseInfo houseInfo = new QHouseInfo("houseInfo");
        return queryFactory.select(houseInfo)
                .from(houseInfo)
                .where(houseInfo.locationOfAgency.eq(locationOfAgency).and(houseInfo.legal.eq(legal)))
                .orderBy(houseInfo.legal.asc())
                .fetch();
    }

    @Override
    public List<HouseInfo> findHouseInfoListByDongSearch(String dongName) {
        final QHouseInfo houseInfo = new QHouseInfo("houseInfo");
        return queryFactory.select(houseInfo)
                .from(houseInfo)
                .where(houseInfo.legal.eq(dongName))
                .fetch();
    }

    @Override
    public List<HouseInfo> findLikedHouseInfoList(List<String> aptCode) {
        final QHouseInfo houseInfo = new QHouseInfo("houseInfo");
        return queryFactory.select(houseInfo)
                .from(houseInfo)
                .where(houseInfo.aptCode.in(aptCode))
                .fetch();
    }

}
