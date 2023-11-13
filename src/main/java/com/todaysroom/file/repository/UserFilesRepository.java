package com.todaysroom.common.file.repository;

import com.todaysroom.common.file.entity.UserFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFilesRepository extends JpaRepository<UserFiles, Long> {

    List<UserFiles> findByPostIdAndFileId(Long postId, Long fileId);
}
