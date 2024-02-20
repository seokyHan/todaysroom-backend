package com.todaysroom.map.entity;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@QueryEntity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GUGUN")
public class Gugun {

    @Id
    @Column(name = "GUGUN_CODE")
    private String gugunCode;

    @Column(name = "GUGUN_NAME")
    private String gugunName;
}
