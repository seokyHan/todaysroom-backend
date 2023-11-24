package com.todaysroom.file.repository;

import com.todaysroom.file.entity.UserFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFilesRepository extends JpaRepository<UserFiles, Long> {

    List<UserFiles> findByPostIdAndFileId(Long postId, Long fileId);
}
