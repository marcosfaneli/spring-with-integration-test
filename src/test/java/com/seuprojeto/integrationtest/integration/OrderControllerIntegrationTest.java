package com.seuprojeto.integrationtest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import com.seuprojeto.integrationtest.integration.app.controller.dto.UpdateOrderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @Autowired
    OrderRepository orderRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ORDER_URL = "/orders";

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
         this.orderRepository.deleteAll();
    }

    @Test
    void shouldReturn200WhenGetAllOrders() throws Exception {
        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertTrue(response.getResponse().getContentAsString().contains("[]"));
    }

    @Test
    void shouldReturn201WhenCreateOrder() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description);
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        Assertions.assertNotNull(orderCreatedDto.id());
        Assertions.assertTrue(pattern.matcher(orderCreatedDto.id()).matches());

        Assertions.assertEquals(description, orderCreatedDto.description());
        Assertions.assertNotNull(orderCreatedDto.createdAt());
        Assertions.assertNotNull(orderCreatedDto.updatedAt());
        Assertions.assertEquals("OPENED", orderCreatedDto.status());
    }

    @Test
    void shouldReturn200WhenPutAnExistentOrder() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description);
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        final String newDescription = "new description";
        final String newStatus = "CLOSED";
        final UpdateOrderDto updateOrderDto = new UpdateOrderDto(newDescription, newStatus);
        final String updatePayload = objectMapper.writeValueAsString(updateOrderDto);

        final MvcResult updateResponse = this.mockMvc.perform(MockMvcRequestBuilders.put(ORDER_URL + "/" + orderCreatedDto.id()).content(updatePayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final OrderCreatedDto updatedOrderCreatedDto = objectMapper.readValue(updateResponse.getResponse().getContentAsString(), OrderCreatedDto.class);
        Assertions.assertEquals(newDescription, updatedOrderCreatedDto.description());
        Assertions.assertEquals(newStatus, updatedOrderCreatedDto.status());
    }
}
