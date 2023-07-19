package com.todaysroom.oauth2.userInfo;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    //생성자 파라미터로 각 소셜 타입별 유저 정보 attributes를 주입받아서
    //각 소셜 타입별 유저 정보 클래스가 소셜 타입에 맞는 attributes를 주입받도록 함
    public OAuth2UserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public abstract String getId(); //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"

    public abstract String getNickname();

    public abstract String getImageUrl();

}
