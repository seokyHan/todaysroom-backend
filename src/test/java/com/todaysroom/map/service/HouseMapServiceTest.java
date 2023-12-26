package com.todaysroom.map.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class HouseMapServiceTest extends CommonTest {

    final MockMvc mvc;

    public HouseMapServiceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
        this.mvc = mockMvc;
    }

    @Test
    void getHouseInfo() throws Exception{

        log.info("##result : {}", this.mvc.perform(put("/map/getHouseInfo")));
    }
}