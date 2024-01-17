package com.todaysroom.map.repository;

import com.todaysroom.map.entity.GugunCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GugunRepository extends JpaRepository<GugunCode, String> {

    String QUERY_GET_GUGUN_IN_SIDO = "SELECT LEFT(GUGUN_CODE,5) GUGUN_CODE, GUGUN_NAME " +
                                       "FROM GUGUN_CODE " +
                                      "WHERE LEFT(GUGUN_CODE,2) = :sidoCode " +
                                   "ORDER BY GUGUN_CODE";

    @Query(value = QUERY_GET_GUGUN_IN_SIDO, nativeQuery = true)
    List<gugunProjection> getGugunInSidoCode(@Param("sidoCode") String sidoCode);


    interface gugunProjection {
        String getGUGUN_CODE();
        String getGUGUN_NAME();
    }
}
