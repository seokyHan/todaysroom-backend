package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Dong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DongRepository extends JpaRepository<Dong, Long>, DongRepositoryCustom{

}
