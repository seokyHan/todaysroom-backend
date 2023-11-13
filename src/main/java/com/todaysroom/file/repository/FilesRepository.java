package com.todaysroom.common.file.repository;

import com.todaysroom.common.file.entity.FilesLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<FilesLocation, Long> {
}
