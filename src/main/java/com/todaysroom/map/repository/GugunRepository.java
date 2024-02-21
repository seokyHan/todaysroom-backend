package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Gugun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GugunRepository extends JpaRepository<Gugun, String> {

}
