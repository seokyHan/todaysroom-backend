package com.todaysroom.map.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;


@Slf4j
class HouseMapServiceTest extends CommonTest {

    final MockMvc mvc;

    public HouseMapServiceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
        this.mvc = mockMvc;
    }

    @Test
    void getsidoList() throws Exception{
        var uri = fromPath("/map/sido");
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getGugunList() throws Exception{
        var uri = fromPath("/map/gugun").queryParam("sidoCode", "11000");
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getDongList() throws Exception{
        String request = """
                {
                    "sidoName" : "서울특별시",
                    "gugunName" : "종로구"
                }
                """;
        var uri = fromPath("/map/dong");
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        requestBuilder.content(request);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getHouseInfoListByGugun() throws Exception{
        String request = """
                {
                    "sidoName" : "서울특별시",
                    "gugunName" : "종로구"
                }
                """;
        var uri = fromPath("/map/gugun/house");
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        requestBuilder.content(request);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void gethouseInfoListByDong() throws Exception{
        String request = """
                {
                    "sidoName" : "서울특별시",
                    "gugunName" : "종로구",
                    "dongName" : "숭인동"
                }
                """;
        var uri = fromPath("/map/dong/house");
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        requestBuilder.content(request);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getHouseInfoListByDongSearch() throws Exception{
        var uri = fromPath("/map/dong-search").queryParam("dongName", "마장동").encode(StandardCharsets.ISO_8859_1);
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getHouseInfoListRecommend() throws Exception{
        var uri = fromPath("/map/recommend").queryParam("dongName", "신당동").encode(StandardCharsets.ISO_8859_1);
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getUserLikedHouseListByGuGun() throws Exception{
        String request = """
                {
                    "sidoName" : "서울특별시",
                    "gugunName" : "종로구"
                }
                """;
        var uri = fromPath("/map/gu/liked").queryParam("userId", 1);
        var builder = get(uri.toUriString());
        var requestBuilder = getRequestBuilder(builder);
        requestBuilder.content(request);
        mvc.perform(requestBuilder).andDo(print()).andExpect(status().isOk());
    }
}