package com.todaysroom.common.file.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.todaysroom.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFiles extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location")
    @JsonBackReference(value = "userFiles-files")
    private FilesLocation file;

    private Long postId;

    private String originalFilename;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String contentType;

    @Builder
    public UserFiles(Long id, FilesLocation file, Long postId, String originalFilename, String fileName, String filePath, Long fileSize, String contentType) {
        this.id = id;
        this.file = file;
        this.postId = postId;
        this.originalFilename = originalFilename;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }
}
