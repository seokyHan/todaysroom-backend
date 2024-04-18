package com.todaysroom.likeHouse.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todaysroom.likeHouse.entity.QLikeHouse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeHouseRepositoryImpl implements LikeHouseRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByUserIdAndAptCode(Long userId, String aptCode) {
        QLikeHouse likeHouse = new QLikeHouse("likeHouse");
        queryFactory.delete(likeHouse)
                .where(likeHouse.user.id.eq(userId), likeHouse.aptCode.eq(aptCode))
                .execute();
    }
}
