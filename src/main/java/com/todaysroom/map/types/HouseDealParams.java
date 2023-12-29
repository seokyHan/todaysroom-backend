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
    DEAL_YMD("DEAL_YMD");

    private final String key;

}
