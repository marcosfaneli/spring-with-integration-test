package com.seuprojeto.integrationtest.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    private static final String ORDER_URL = "/orders";

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturn200WhenGetAllOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void shouldReturn201WhenCreateOrder() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content("{\"description\": \"some description\"}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println(response.getResponse().getContentAsString());

        Assertions.assertTrue(response.getResponse().getContentAsString().contains("description"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("some description"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("id"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("createdAt"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("updatedAt"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("status"));
        Assertions.assertTrue(response.getResponse().getContentAsString().contains("OPENED"));
    }
}
