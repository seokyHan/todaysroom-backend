package com.todaysroom.map.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HouseDealParams {
    SERVICE_KEY("serviceKey"),
    PAGE_NO("pageNo"),
    NUM_OF_ROWS("numOfRows"),
    LAWD_CD("LAWD_CD"),
    DEAL_YMD("DEAL_YMD"),
    LOCAL_OF_AGENCY("중개사소재지"),
    LEGAL_BUILDING("법정동"),
    ROAD_NAME("도로명"),
    ROAD_NAME_BUILDING_CODE("도로명건물본번호코드");

    private final String key;

}
