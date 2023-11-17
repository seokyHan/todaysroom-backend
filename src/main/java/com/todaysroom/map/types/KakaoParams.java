package com.todaysroom.map.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KakaoParams {
    KAKAO_AK("KakaoAK "),
    KAKAO_QUERY("query"),
    LONGITUDE("x"),
    LATITUDE("y"),
    AUTH("Authorization");

    private final String key;
}
