package com.todaysroom.file.dto;

import com.todaysroom.file.entity.FilesLocation;
import com.todaysroom.file.entity.UserFiles;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UserFileRequestDto(Long id,
                                 Long postId,
                                 Long fileSize,
                                 FilesLocation file,
                                 String fileName,
                                 String originalFilename,
                                 String contentType,
                                 String filePath,
                                 List<MultipartFile> fileList) {

    @Builder
    public UserFileRequestDto {
    }

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
