package com.company.iot.data.producer;

import com.company.iot.data.model.json.Data;
import com.company.iot.data.util.JsonGenerator;
import com.company.iot.data.util.ProducerConfigCreator;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataGenerator implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("DataGenerator");
    private Gson gson = new Gson();
    //private ProducerRecord<String, String> producerRecord = null;
    private KafkaProducer<String, String> kafkaProducer = null;
    private String topicName;

    public DataGenerator(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void run() {
        kafkaProducer = new KafkaProducer<>(ProducerConfigCreator.getConfig());

        while (true) {
            Data data = gson.fromJson(JsonGenerator.generate(), Data.class);
            kafkaProducer.send(new ProducerRecord<>(topicName, data.getDeviceId(), JsonGenerator.generate()));
            kafkaProducer.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.info("Error occurred when producing data", e);
            }
        }
    }
}
