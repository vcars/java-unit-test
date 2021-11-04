package com.example.belajar.unittest.unittest.controller;

import com.alibaba.fastjson.JSON;
import com.example.belajar.unittest.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.unittest.model.response.CatalogResponse;
import com.example.belajar.unittest.unittest.model.response.ListCatalogResponse;
import com.example.belajar.unittest.unittest.service.*;
import com.example.belajar.unittest.unittest.util.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private GetCatalogService getCatalogService;

    @Mock
    private LoginService loginService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private RegisterService registerService;

    @Mock
    private SaveCatalogService saveCatalogService;

    private String accessToken = "accessToken";

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .build();
    }

    @Test
    void shoudlSuccessGetCatalogServiceTest() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        List<CatalogResponse> catalogResponseList = new ArrayList<>();
        catalogResponseList.add(CatalogResponse.builder()
                .catalogName("Category Name 1")
                .price(1000.00)
                .stock(200)
                .build());
        when(getCatalogService.execute(accessTokenRequest)).thenReturn(ListCatalogResponse.builder().catalogResponseList(catalogResponseList).build());
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .get("/api/customers/v1/catalog")
                .headers(setHeaders())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        Response expected = new Response(ListCatalogResponse.builder().catalogResponseList(catalogResponseList).build(), "Ini data anda", true);
        assertEquals(200, actualResponse.getStatus());
        assertEquals(JSON.toJSONString(expected), actualResponse.getContentAsString());
        verify(getCatalogService).execute(AccessTokenRequest.builder().accessToken(accessToken).build());
    }

    @Test
    void shoudlFailedGetCatalogServiceTest() throws Exception {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().accessToken(accessToken).build();
        when(getCatalogService.execute(accessTokenRequest)).thenReturn(ListCatalogResponse.builder().catalogResponseList(new ArrayList<>()).build());
        MockHttpServletRequestBuilder payload = MockMvcRequestBuilders
                .get("/api/customers/v1/catalog")
                .headers(setHeaders())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse actualResponse = mockMvc.perform(payload)
                .andDo(print())
                .andReturn().getResponse();
        assertEquals(404, actualResponse.getStatus());
        assertEquals("Data tidak ditemukan", actualResponse.getErrorMessage());
        verify(getCatalogService).execute(AccessTokenRequest.builder().accessToken(accessToken).build());
    }


    private HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        return headers;
    }
}
