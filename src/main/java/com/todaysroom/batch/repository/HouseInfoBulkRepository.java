package com.todaysroom.batch.repository;

import com.todaysroom.map.entity.HouseInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HouseInfoBulkRepository {

    private static final String INSERT_QUERY = """
            INSERT INTO HOUSE_INFO (LOCAL_CODE, EXCLUSIVE_AREA, BUILD_YEAR, LEGAL, ROAD_NAME, FLOOR,
                                    YEAR, MONTH, DAY, LNG, LAT , AMOUNT,
                                    LOCATION_OF_AGENCY, APT_NAME, INSERT_DATE)
                      VALUES (
                               ?,?,?,?,?,?,
                               ?,?,?,?,?,?,
                               ?,?,now()
                             )
            """;

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<HouseInfo> houseInfoList){
        jdbcTemplate.batchUpdate(INSERT_QUERY,
                new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int rowIndex) throws SQLException {
                int idx = 0;
                ps.setInt(++idx, houseInfoList.get(rowIndex).getLocalCode());
                ps.setDouble(++idx, houseInfoList.get(rowIndex).getExclusiveArea());
                ps.setString(++idx, houseInfoList.get(rowIndex).getBuildYear());
                ps.setString(++idx, houseInfoList.get(rowIndex).getLegal());
                ps.setString(++idx, houseInfoList.get(rowIndex).getRoadName());
                ps.setInt(++idx, houseInfoList.get(rowIndex).getFloor());
                ps.setInt(++idx, houseInfoList.get(rowIndex).getYear());
                ps.setInt(++idx, houseInfoList.get(rowIndex).getMonth());
                ps.setInt(++idx, houseInfoList.get(rowIndex).getDay());
                ps.setDouble(++idx, houseInfoList.get(rowIndex).getLng());
                ps.setDouble(++idx, houseInfoList.get(rowIndex).getLat());
                ps.setString(++idx, houseInfoList.get(rowIndex).getAmount());
                ps.setString(++idx, houseInfoList.get(rowIndex).getLocationOfAgency());
                ps.setString(++idx, houseInfoList.get(rowIndex).getAptName());
            }

            @Override
            public int getBatchSize() {
                return houseInfoList.size();
            }
        });
    }
}
