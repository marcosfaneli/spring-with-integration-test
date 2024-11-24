package com.seuprojeto.integrationtest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.app.controller.dto.UpdateOrderDto;
import com.seuprojeto.integrationtest.infra.database.OrderRepository;
import com.seuprojeto.integrationtest.infra.producer.dto.OrderCreatedMessage;
import com.seuprojeto.integrationtest.infra.producer.dto.OrderUpdatedMessage;
import com.seuprojeto.integrationtest.shared.JacksonConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "created-order", "updated-order" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class OrderControllerIntegrationTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    MockMvc mockMvc;

    private static final ObjectMapper objectMapper = JacksonConfig.objectMapper();

    private static final String ORDER_URL = "/orders";

    private static WireMockServer wireMockServer;

    private DefaultKafkaConsumerFactory<String, String> consumerFactory;

    @BeforeAll
    static void setUpWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        wireMockServer.start();

        final CreateOrderDto createOrderDto = new CreateOrderDto("some description", "1");
        final String customerCode = createOrderDto.customerCode();

        wireMockServer.stubFor(get(urlEqualTo("/api/customers/" + customerCode))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"" + customerCode + "\", \"name\": \"John Doe\", \"email\": \"john@email.com\"}")));
    }

    @AfterAll
    static void tearDownWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        this.orderRepository.deleteAll();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("sender", "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Test
    void shouldReturn200WhenGetAllOrders() throws Exception {
        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertTrue(response.getResponse().getContentAsString().contains("{\"page\":0,\"size\":10,\"total\":0,\"data\":[]}"));
    }

    @Test
    void shouldReturn200WhenGetAllOrdersWithQuery() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "1");
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "?query=some"))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertTrue(response.getResponse().getContentAsString().contains(description));
    }

    @Test
    void shouldReturn201WhenCreateOrder() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "1");
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
        Assertions.assertEquals("1", orderCreatedDto.customerCode());
        Assertions.assertEquals("John Doe", orderCreatedDto.customerName());
        Assertions.assertEquals("john@email.com", orderCreatedDto.customerEmail());

        try (var consumer = consumerFactory.createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "created-order");
            ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "created-order");

            Assertions.assertNotNull(singleRecord);

            OrderCreatedMessage orderCreatedMessage = objectMapper.readValue(singleRecord.value(), OrderCreatedMessage.class);
            Assertions.assertEquals(orderCreatedDto.id(), orderCreatedMessage.getId());
            Assertions.assertEquals(orderCreatedDto.customerCode(), orderCreatedMessage.getCustomerId());
            Assertions.assertNotNull(orderCreatedMessage.getCreatedAt());
            Assertions.assertEquals("OPENED", orderCreatedMessage.getStatus());
        }
    }

    @Test
    void shouldReturn404WhenCreateOrderWithNonExistentCustomer() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "2");
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void shouldReturn200WhenPutAnExistentOrder() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "1");
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

        try (var consumer = consumerFactory.createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "updated-order");
            ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "updated-order");

            Assertions.assertNotNull(singleRecord);

            OrderUpdatedMessage orderUpdatedMessage = objectMapper.readValue(singleRecord.value(), OrderUpdatedMessage.class);
            Assertions.assertEquals(orderCreatedDto.id(), orderUpdatedMessage.getId());
            Assertions.assertEquals(orderCreatedDto.customerCode(), orderUpdatedMessage.getCustomerId());
            Assertions.assertNotNull(orderUpdatedMessage.getCreatedAt());
            Assertions.assertNotNull(orderUpdatedMessage.getUpdatedAt());
            Assertions.assertEquals(newStatus, orderUpdatedMessage.getStatus());
        }
    }

    @Test
    void shouldReturn200WhenGetAnExistentOrder() throws Exception {
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "1");
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
        final String description = "some description";
        final CreateOrderDto createOrderDto = new CreateOrderDto(description, "1");
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

    @Test
    void shouldReturn400WhenUpdateOrderWithInvalidStatus() throws Exception {
        final CreateOrderDto createOrderDto = new CreateOrderDto("some description", "1");
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        final MvcResult response = this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final OrderCreatedDto orderCreatedDto = objectMapper.readValue(response.getResponse().getContentAsString(), OrderCreatedDto.class);

        final UpdateOrderDto updateOrderDto = new UpdateOrderDto("new description", "INVALID");
        final String updatePayload = objectMapper.writeValueAsString(updateOrderDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put(ORDER_URL + "/" + orderCreatedDto.id()).content(updatePayload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void shouldReturn400WhenCreateOrderWithInvalidDescription() throws Exception {
        final CreateOrderDto createOrderDto = new CreateOrderDto("", "1");
        final String payload = objectMapper.writeValueAsString(createOrderDto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL).content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

}
