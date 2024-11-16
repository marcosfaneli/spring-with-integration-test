package com.seuprojeto.integrationtest.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonSerializerConfig<T> implements Serializer<T> {

    private final ObjectMapper objectMapper = JacksonConfig.objectMapper();

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            if (data == null){
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing to byte[]");
        }
    }
}
