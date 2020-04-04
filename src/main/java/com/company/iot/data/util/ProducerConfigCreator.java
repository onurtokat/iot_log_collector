package com.company.iot.data.util;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

/**
 * ProducerConfigCreator class provides static method for Kafka config
 * @author Onur Tokat
 */
public class ProducerConfigCreator {

    public static Properties getConfig() {
        Properties config = new Properties();
        config.setProperty(org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG, "producer-app_"
                + UUID.randomUUID().toString());
        config.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.setProperty(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        config.setProperty(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return config;
    }
}
