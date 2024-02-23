package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Sido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SidoRepository extends JpaRepository<Sido, String> {

    String QUERY_GET_SIDO = "SELECT LEFT(SIDO_CODE,2) SIDO_CODE, SIDO_NAME" +
                              "FROM SIDO_CODE" +
                          "ORDER BY SIDO_CODE";

    @Query(value = QUERY_GET_SIDO, nativeQuery = true)
    List<sidoProjection> getsido();


    interface sidoProjection {
        String getSIDO_CODE();
        String getSIDO_NAME();
    }


}
