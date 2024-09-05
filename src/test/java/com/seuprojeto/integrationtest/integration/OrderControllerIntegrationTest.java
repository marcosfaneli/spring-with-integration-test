package com.seuprojeto.integrationtest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.fixture.CreateOrderFixture;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import com.seuprojeto.integrationtest.integration.app.controller.dto.UpdateOrderDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ORDER_URL = "/orders";

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUpWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9000));
        wireMockServer.start();

        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto();
        final String customerCode = createOrderDto.customerCode();

        wireMockServer.stubFor(get(urlEqualTo("/api/customers/" + customerCode))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"" + customerCode + "\", \"name\": \"John Doe\"}")));
    }

    @AfterAll
    static void tearDownWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
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
        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto();

        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        Assertions.assertNotNull(orderCreatedDto.id());
        Assertions.assertTrue(pattern.matcher(orderCreatedDto.id()).matches());

        Assertions.assertEquals(createOrderDto.description(), orderCreatedDto.description());
        Assertions.assertEquals(createOrderDto.customerCode(), orderCreatedDto.customerCode());
        Assertions.assertNotNull(orderCreatedDto.customerName());
        Assertions.assertNotNull(orderCreatedDto.createdAt());
        Assertions.assertNotNull(orderCreatedDto.updatedAt());
        Assertions.assertEquals("OPENED", orderCreatedDto.status());
    }

    @Test
    void shouldReturn200WhenPutAnExistentOrder() throws Exception {
        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto();
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

    @Test
    void shouldReturn404WhenCreateOrderWithNonExistentCustomer() throws Exception {
        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto("description", "non-existent-customer");

        final String payload = objectMapper.writeValueAsString(createOrderDto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void shouldReturn200WhenGetAnExistentOrder() throws Exception {
        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto();
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        final MvcResult getResponse = this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/" + orderCreatedDto.id()))
                .andExpect(status().isOk())
                .andReturn();

        final OrderCreatedDto getOrderCreatedDto = objectMapper.readValue(getResponse.getResponse().getContentAsString(), OrderCreatedDto.class);
        Assertions.assertEquals(orderCreatedDto.id(), getOrderCreatedDto.id());
        Assertions.assertEquals(orderCreatedDto.description(), getOrderCreatedDto.description());
        Assertions.assertEquals(orderCreatedDto.status(), getOrderCreatedDto.status());
    }

    @Test
    void shouldReturn204WhenDeleteAnExistentOrder() throws Exception {
        final CreateOrderDto createOrderDto = CreateOrderFixture.createOrderDto();
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(ORDER_URL + "/" + orderCreatedDto.id()))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/" + orderCreatedDto.id()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void shouldReturn404WhenGetAnNonExistentOrder() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/123"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
