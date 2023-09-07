package com.todaysroom.common.file.repository;

import com.todaysroom.common.file.entity.UserFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilesRepository extends JpaRepository<UserFiles, Long> {
}
