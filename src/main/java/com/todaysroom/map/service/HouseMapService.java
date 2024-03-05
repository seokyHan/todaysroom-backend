package com.todaysroom.map.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.batch.dto.GuGunDto;
import com.todaysroom.map.props.HouseDealProperties;
import com.todaysroom.map.props.KaKaoProperties;
import com.todaysroom.map.dto.HouseInfoDto;
import com.todaysroom.map.repository.GugunRepository;
import com.todaysroom.map.types.AptKey;
import com.todaysroom.map.types.KakaoParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.todaysroom.map.types.AptKey.*;
import static com.todaysroom.map.types.HouseDealParams.*;
import static com.todaysroom.map.types.KakaoParams.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class HouseMapService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final HouseDealProperties houseDealProperties;
    private final KaKaoProperties kaKaoProperties;
    private final GugunRepository gugunRepository;
    private final String PAGE = "1";
    private final String ROWS = "10";
    private final String LNG = "lng";
    private final String LAT = "lat";
    private final String KAKAO_JSON_KEY = "documents";
    public record CoordinatesDto(String x, String y){}

    public Mono<List<List<HouseInfoDto>>> getHouseInfo(){
        return Flux.fromIterable(gugunRepository.findAll().stream().map(GuGunDto::from).collect(Collectors.toUnmodifiableList()))
                .flatMap(gugun -> {
                    UriComponents url = buildHouseInfoUrl(gugun.guGunCode());
                    return fetchResponse(url)
                            .flatMap(this::extractResponseBody)
                            .flatMap(responseBody -> addLatLng(responseBody));
                })
                .collectList();
    }

    private UriComponents buildHouseInfoUrl(String gugunCode){
        Map<String, String> params = Map.of(
                PAGE_NO.getKey(), PAGE,
                NUM_OF_ROWS.getKey(), ROWS,
                LAWD_CD.getKey(), gugunCode,
                DEAL_YMD.getKey(), "202402"
        );

        return makeUri(houseDealProperties.host(), houseDealProperties.secret(), params);
    }

    private Mono<JSONObject> fetchResponse(UriComponents url) {
        return webClient.get()
                .uri(url.toString())
                .retrieve()
                .bodyToMono(String.class)
                .map(XML::toJSONObject);
    }

    private Mono<JSONObject> extractResponseBody(JSONObject response) {
        return Mono.justOrEmpty(response.getJSONObject("response"))
                .filterWhen(body -> Mono.just(body.has("body")))
                .switchIfEmpty(Mono.empty());
    }

    private Mono<List<HouseInfoDto>> addLatLng(JSONObject responseBody) {
        JSONArray itemList = responseBody.getJSONObject("items").getJSONArray("item");
        return Flux.fromIterable(itemList)
                .filter(item -> !isContainComma((JSONObject) item))
                .flatMap(item -> translateAndProcessItem((JSONObject) item))
                .collectList();
    }

    private Mono<HouseInfoDto> translateAndProcessItem(JSONObject item) {
        JSONObject translatedItem = translateJsonKey(item);
        String location = getLocation(item);
        return getLatLng(location)
                .map(coordinatesDto -> {
                    translatedItem.put(LNG, coordinatesDto.x());
                    translatedItem.put(LAT, coordinatesDto.y());
                    try {
                        return objectMapper.readValue(translatedItem.toString(), HouseInfoDto.class);
                    } catch (IOException e) {
                        log.error("Error processing JSON: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }

    private boolean isContainComma(JSONObject item) {
        String locationOfAgency = item.optString(LOCAL_OF_AGENCY.getKoreanKey(), "");
        String legalBuilding = String.valueOf(item.opt(LEGAL_BUILDING.getKoreanKey()));
        String roadName = item.optString(ROAD_NAME.getKoreanKey(), "");
        String roadNameBuildingCode = item.optString(ROAD_NAME_BUILDING_CODE.getKoreanKey(), "").replace("0", "");
        String location = String.join(" ", locationOfAgency, legalBuilding, roadName, roadNameBuildingCode);
        return location.contains(",");
    }

    private Mono<CoordinatesDto> getLatLng(String location) {
        String apiKey = String.join(" ", KakaoParams.KAKAO_AK.getKey() + kaKaoProperties.secret());
        Map<String, String> params = Map.of(KAKAO_QUERY.getKey(), location);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTH.getKey(), apiKey);
        UriComponents url = makeUri(kaKaoProperties.host(), apiKey, params);

        Mono<CoordinatesDto> coordinatesDto = webClient.get()
                .uri(url.toString())
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .map(JSONObject::new)
                .flatMap(json -> {
                    JSONArray documents = json.getJSONArray(KAKAO_JSON_KEY);
                    String x = documents.getJSONObject(0).getString(LONGITUDE.getKey());
                    String y = documents.getJSONObject(0).getString(LATITUDE.getKey());
                    return Mono.just(new CoordinatesDto(x,y));
                });

        return coordinatesDto;
    }

    private UriComponents makeUri(String host, String apiKey, Map<String, String> params){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host)
                .queryParam(SERVICE_KEY.getKey(), apiKey);
        params.forEach(builder::queryParam);
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

        return translateItem;
    }

    private String getLocation(JSONObject item){
        return String.join(" ",
                item.optString(LOCAL_OF_AGENCY.getKoreanKey(), ""),
                item.optString(LEGAL.getKoreanKey(), ""),
                item.optString(ROAD_NAME.getKoreanKey(), ""),
                item.optString(ROAD_NAME_BUILDING_CODE.getKoreanKey(), "")
        ).trim();
    }

}
