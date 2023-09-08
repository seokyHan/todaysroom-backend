package com.todaysroom.common.file.dto;

import com.todaysroom.common.file.entity.Files;
import com.todaysroom.common.file.entity.UserFiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UserFileDto(Long id,
                          Long postId,
                          Long fileSize,
                          Files file,
                          String fileName,
                          String originalFilename,
                          String contentType,
                          String filePath,
                          List<MultipartFile> files) {

    public UserFiles toUserFilesEntity(){
        return UserFiles.builder()
                .id(id)
                .file(file)
                .postId(postId)
                .originalFilename(originalFilename)
                .fileName(fileName)
                .filePath(filePath)
                .fileSize(fileSize)
                .contentType(contentType)
                .build();
    }
}
