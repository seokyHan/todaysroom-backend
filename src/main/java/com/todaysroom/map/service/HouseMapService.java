package com.todaysroom.map.service;

import com.todaysroom.map.props.HouseDealProperties;
import com.todaysroom.map.props.KaKaoProperties;
import com.todaysroom.map.dto.CoordinatesDto;
import com.todaysroom.map.types.AptKey;
import com.todaysroom.map.types.KakaoParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.todaysroom.map.types.AptKey.*;
import static com.todaysroom.map.types.HouseDealParams.*;
import static com.todaysroom.map.types.KakaoParams.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class HouseMapService {

    private final RestTemplate restTemplate;
    private final HouseDealProperties houseDealProperties;
    private final KaKaoProperties kaKaoProperties;
    private static final String PAGE = "1";
    private static final String ROWS = "10";
    private static final String LNG = "lng";
    private static final String LAT = "lat";
    private static final String KAKAO_JSON_KEY = "documents";

    public String getHouseInfo() {
        UriComponents url = buildHouseInfoUrl();

        JSONObject response = fetchResponse(url);
        JSONObject responseBody = extractResponseBody(response);
        JSONArray itemList = addLatLng(responseBody);

        return itemList.toString();
    }

    private UriComponents buildHouseInfoUrl(){
        Map<String, String> params = Map.of(
                PAGE_NO.getKey(), PAGE,
                NUM_OF_ROWS.getKey(), ROWS,
                LAWD_CD.getKey(), "11140",
                DEAL_YMD.getKey(), "202310"
        );

        return makeUri(houseDealProperties.host(), houseDealProperties.secret(), params);
    }

    private JSONObject fetchResponse(UriComponents url){
        String response = restTemplate.getForEntity(url.toString(), String.class).getBody();
        return XML.toJSONObject(response);
    }

    private JSONObject extractResponseBody(JSONObject response){
        return response.getJSONObject("response").getJSONObject("body");
    }

    private JSONArray addLatLng(JSONObject responseBody){
        JSONArray itemList = responseBody.getJSONObject("items").getJSONArray("item");
        JSONArray translatedItemList = new JSONArray();

        for(int i = 0; i < itemList.length(); i++){
            JSONObject item = itemList.getJSONObject(i);

            String locationOfAgency = item.getString(LOCAL_OF_AGENCY.getKoreanKey());
            String legalBuilding = item.getString(LEGAL_BUILDING.getKoreanKey());
            String roadName = item.getString(ROAD_NAME.getKoreanKey());
            String roadNameBuildingCode = item.getString(ROAD_NAME_BUILDING_CODE.getKoreanKey()).replace("0","");

            String location = String.join(" ", locationOfAgency, legalBuilding, roadName, roadNameBuildingCode);
            if(location.contains(",")){
                continue;
            }

            JSONObject translatedItem = translateJsonKey(item);
            translatedItemList.put(i, translatedItem);
        }

        return translatedItemList;
    }

    private CoordinatesDto getLatLng(String location) {
        String apiKey = String.join(" ", KakaoParams.KAKAO_AK.getKey() + kaKaoProperties.secret());

        Map<String, String> params = Map.of(
                KAKAO_QUERY.getKey(), location
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTH.getKey(), apiKey);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        UriComponents url = makeUri(kaKaoProperties.host(), apiKey, params);

        String body = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, String.class).getBody();
        JSONObject json = new JSONObject(body);

        JSONArray documents = json.getJSONArray(KAKAO_JSON_KEY);
        String x = documents.getJSONObject(0).getString(LONGITUDE.getKey());
        String y = documents.getJSONObject(0).getString(LATITUDE.getKey());

        return CoordinatesDto.of(x, y);
    }

    private UriComponents makeUri(String host, String apiKey, Map<String, String> params){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host)
                .queryParam(SERVICE_KEY.getKey(), apiKey);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private JSONObject translateJsonKey(JSONObject item) {
        JSONObject translateItem = new JSONObject();

        for (AptKey aptKey : AptKey.values()) {
            String englishKey = aptKey.getEnglishKey();
            String koreanKey = aptKey.getKoreanKey();

            if (item.has(koreanKey)) {
                Object value = item.get(koreanKey);
                translateItem.put(englishKey, value);
            }
        }

        if (translateItem.has(ROAD_NAME_BUILDING_CODE.getEnglishKey())) {
            String roadNameBuildingCode = translateItem.getString(ROAD_NAME_BUILDING_CODE.getEnglishKey()).replace("0", "");
            translateItem.put(ROAD_NAME_BUILDING_CODE.getEnglishKey(), roadNameBuildingCode);
        }

        CoordinatesDto coordinatesDto = getLatLng(getLocation(item));
        translateItem.put(LNG, coordinatesDto.x());
        translateItem.put(LAT, coordinatesDto.y());

        return translateItem;
    }

    private String getLocation(JSONObject item){
        StringBuilder location = new StringBuilder();
        for(AptKey aptKey : List.of(LOCAL_OF_AGENCY, LEGAL, ROAD_NAME, ROAD_NAME_BUILDING_CODE)){
            if(item.has(aptKey.getKoreanKey())){
                location.append(item.getString(aptKey.getKoreanKey())).append(" ");
            }
        }
        return location.toString();
    }

}
