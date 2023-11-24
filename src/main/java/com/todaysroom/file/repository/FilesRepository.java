package com.todaysroom.file.repository;

import com.todaysroom.file.entity.FilesLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<FilesLocation, Long> {
}
