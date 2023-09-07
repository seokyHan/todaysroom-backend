package com.todaysroom.common.file.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.todaysroom.common.BaseTimeEntity;
import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.user.entity.Authority;
import com.todaysroom.user.entity.UserEntity;
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
    @JoinColumn(name = "file_location")
    @JsonBackReference(value = "userFiles-files")
    private Files file;

    private Long postId;

    private String origFilename;

    private String fileName;

    private String filePath;

    private String fileSize;

    @Builder
    public UserFiles(Long id, Long postId, String origFilename, String fileName, String filePath, String fileSize) {
        this.id = id;
        this.postId = postId;
        this.origFilename = origFilename;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}
