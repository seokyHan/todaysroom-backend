package com.todaysroom.map.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todaysroom.map.entity.Dong;
import com.todaysroom.map.entity.QDong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static com.todaysroom.map.entity.QDong.dong;

@Repository
@RequiredArgsConstructor
public class DongRepositoryImpl implements DongRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Dong> findDongList(String sidoName, String guGunName) {
        final QDong dong = new QDong("dong");
        return queryFactory.select(dong)
                .from(dong)
                .where(dong.sidoName.eq(sidoName).and(dong.gugunName.eq(guGunName)))
                .orderBy(dong.dongName.asc())
                .fetch();
    }
}
