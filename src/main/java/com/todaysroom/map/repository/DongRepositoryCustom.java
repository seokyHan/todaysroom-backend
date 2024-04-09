package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Dong;

import java.util.List;

public interface DongRepositoryCustom {
    List<Dong> findDongList(String sidoName, String guGunName);
}
