package com.company.iot.data.util;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ProducerConfigCreatorTest {

    @Test
    void getConfig() {
        Properties properties = ProducerConfigCreator.getConfig();
        assertTrue(properties.getProperty("client.id").contains("producer-app_"));
        assertTrue(properties.getProperty("bootstrap.servers").equals("localhost:9092"));
        assertTrue(properties.getProperty("key.serializer") == StringSerializer.class.getName());
        assertTrue(properties.getProperty("value.serializer") == StringSerializer.class.getName());
        assertNotNull(properties);
        assertTrue(properties.size() == 4);
    }
}