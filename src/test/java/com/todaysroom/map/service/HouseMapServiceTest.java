package com.todaysroom.map.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.CommonTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@Slf4j
class HouseMapServiceTest extends CommonTest {

    final MockMvc mvc;

    public HouseMapServiceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
        this.mvc = mockMvc;
    }

    @Test
    void getHouseInfo() throws Exception{
        this.mvc.perform(get("/map/getHouseInfo")).andDo(print()).andExpect(status().isOk());

    }
}