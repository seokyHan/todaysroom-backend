package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Dong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DongRepository extends JpaRepository<Dong, Long> {

    String QUERY_GET_DONG_IN_GUGUN = "SELECT DISTINCT DONG_NAME, DONG_CODE " +
            "FROM DONG_CODE " +
            "WHERE GUGUN_NAME = :gugunName " +
            "ORDER BY DONG_NAME";


    @Query(value = QUERY_GET_DONG_IN_GUGUN, nativeQuery = true)
    List<SidoRepository.sidoProjection> getDongInGugun(@Param("gugunName") String gugunName);


    interface dongProjection {
        String getDONG_CODE();
        String getDONG_NAME();
    }

}
