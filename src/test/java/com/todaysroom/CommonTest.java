package com.todaysroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class CommonTest {
    public final MockMvc mockMvc;
    public final ObjectMapper objectMapper;
    public final String token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0MTIzQG5hdmVyLmNvbSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE3MDQyNTE5MTd9.HQi3w58310XSxiRRoWDYrBL_EB_pNEjuRdUisK9ODsfsPOI14JYZW_CSjj5Xn7zNupBjm9tdD67fmLTKaw3Z3g";

    public ResultActions mvcAction(String content, MockHttpServletRequestBuilder methodType) throws Exception {
        MockHttpServletRequestBuilder authorization = getAuthorizationRequestBuilder(methodType);
        MockHttpServletRequestBuilder contents = authorization.contentType(APPLICATION_JSON_VALUE).content(content);
        return mockMvc.perform(contents);
    }

    @NotNull
    private MockHttpServletRequestBuilder getAuthorizationRequestBuilder(MockHttpServletRequestBuilder methodType){
        return methodType.header("Authorization", "Bearer " + token);
    }

    public void postTest(String path, String content) throws Exception {
        mvcAction(content, post(path)).andDo(print()).andExpect(status().isOk());
    }

    public void getTest(String path, String content) throws Exception {
        mvcAction(content, get(path)).andDo(print()).andExpect(status().isOk());
    }

}
