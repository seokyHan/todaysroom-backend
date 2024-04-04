package com.todaysroom.map.repository;

import com.todaysroom.map.entity.Sido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SidoRepository extends JpaRepository<Sido, String> {

}
