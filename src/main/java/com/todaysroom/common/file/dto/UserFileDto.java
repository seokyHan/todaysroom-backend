package com.todaysroom.common.file.dto;

public record UserFileDto(Long fileId,
                          Long postId,
                          Long fileSize,
                          String fileName,
                          String contentType,
                          String fileUpdateStr) {
}
