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
@Table(name = "SIDO")
public class Sido {

    @Id
    @Column(name = "SIDO_CODE")
    private String sidoCode;

    @Column(name = "SIDO_NAME")
    private String sidoName;

}
