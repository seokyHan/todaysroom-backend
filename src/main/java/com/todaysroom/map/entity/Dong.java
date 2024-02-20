package com.todaysroom.map.entity;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@QueryEntity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DONG")
public class Dong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "DONG_CODE")
    private String dongCode;

    @Column(name = "SIDO_NAME")
    private String sidoName;

    @Column(name = "GUGUN_NAME")
    private String gugunName;

    @Column(name = "DONG_NAME")
    private String dongName;
}
