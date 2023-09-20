package com.todaysroom.common.file.repository;

import com.todaysroom.common.file.entity.Files;
import com.todaysroom.common.file.entity.UserFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
