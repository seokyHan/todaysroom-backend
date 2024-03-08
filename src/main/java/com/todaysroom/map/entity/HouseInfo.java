package com.todaysroom.map.entity;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@QueryEntity
@Table(name = "HOUSE_INFO")
public class HouseInfo {

    @Id
    private Long id;

    @Column(name = "EXCLUSIVE_AREA")
    private Double exclusiveArea;

    @Column(name = "BUILD_YEAR")
    private String buildYear;

    @Column(name = "LEGAL")
    private String legal;

    @Column(name = "ROAD_NAME")
    private String roadName;

    @Column(name = "FLOOR")
    private int floor;

    @Column(name = "YEAR")
    private int year;

    @Column(name = "MONTH")
    private int month;

    @Column(name = "DAY")
    private int day;

    @Column(name = "LNG")
    private Double lng;

    @Column(name = "LAT")
    private Double lat;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "LOCATION_OF_AGENCY")
    private String locationOfAgency;

    @Column(name = "APT_NAME")
    private String aptName;

    @Column(name = "INSERT_DATE")
    private Timestamp insertDate;

    @Builder
    public HouseInfo(Long id, Double exclusiveArea, String buildYear, String legal, String roadName, int floor, int year, int month, int day, Double lng, Double lat, String amount, String locationOfAgency, String aptName, Timestamp insertDate) {
        this.id = id;
        this.exclusiveArea = exclusiveArea;
        this.buildYear = buildYear;
        this.legal = legal;
        this.roadName = roadName;
        this.floor = floor;
        this.year = year;
        this.month = month;
        this.day = day;
        this.lng = lng;
        this.lat = lat;
        this.amount = amount;
        this.locationOfAgency = locationOfAgency;
        this.aptName = aptName;
        this.insertDate = insertDate;
    }
}
