package io.github.k_tomaszewski.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.k_tomaszewski.discovery.model.DiscoveryRequest;
import io.github.k_tomaszewski.discovery.model.DiscoveryResponse;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JsonSerialization {

    private final ObjectMapper objectMapper;

    public JsonSerialization() {
        this.objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public <T> T deserialize(ByteBuffer bytes, Class<T> type) {
        try {
            return objectMapper.readValue(bytes.array(), bytes.position(), bytes.limit() - bytes.position(), type);
        } catch (IOException e) {
            throw new RuntimeException("Cannot deserialize DiscoveryRequest", e);
        }
    }

    public ByteBuffer serialize(Object response) {
        try {
            return ByteBuffer.wrap(objectMapper.writeValueAsBytes(response));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize " + response.getClass().getName(), e);
        }
    }
}
