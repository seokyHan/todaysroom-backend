package com.todaysroom.common.file.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileLocation;

    @OneToMany
    @JoinColumn(name = "file_location")
    @JsonBackReference(value = "userFiles-files")
    private List<UserFiles> fileList;


}