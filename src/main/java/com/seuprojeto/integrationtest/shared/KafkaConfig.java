package com.seuprojeto.integrationtest.shared;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig<T> {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;

    @Value(value = "${spring.kafka.username}")
    private String username;

    @Value(value = "${spring.kafka.password}")
    private String password;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, T> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonSerializerConfig.class);
//        configProps.put("sasl.mechanism", "SCRAM-SHA-256");
//        configProps.put("security.protocol", "SASL_SSL");
//        configProps.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\""+username+"\" password=\""+password+"\";");
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, groupId);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
