package com.todaysroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;
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
    public final String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzQG5hdmVyLmNvbSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE3MTU3MTk5MTF9.Lt-tBEa4qKsOxPxfjRsZaqa5TxmjhR6_HvryT2g641LKfN0R8khD-Lu5e9iD2JNzF32S7rJJiMzdt8xH7dzqTQ";

    public ResultActions mvcAction(String content, MockHttpServletRequestBuilder methodType) throws Exception {
        MockHttpServletRequestBuilder authorization = getAuthorizationRequestBuilder(methodType);
        MockHttpServletRequestBuilder contents = authorization.contentType(APPLICATION_JSON_VALUE).content(content);
        return mockMvc.perform(contents);
    }

    @NotNull
    private MockHttpServletRequestBuilder getAuthorizationRequestBuilder(MockHttpServletRequestBuilder methodType){
        return methodType.header("Authorization", "Bearer " + token);
    }

    public MockHttpServletRequestBuilder getRequestBuilder(MockHttpServletRequestBuilder builder) throws Exception {
        HttpHeaders headers = headers();
        return builder.contentType(APPLICATION_JSON).headers(headers);
    }

    public MultiValueMap<String, String> headerMap(){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", token);
        return headers;
    }

    public HttpHeaders headers(){
        return new HttpHeaders(headerMap());
    }

    public void postTest(String path, String content) throws Exception {
        mvcAction(content, post(path)).andDo(print()).andExpect(status().isOk());
    }

    public void getTest(String path, String content) throws Exception {
        mvcAction(content, get(path)).andDo(print()).andExpect(status().isOk());
    }

}
