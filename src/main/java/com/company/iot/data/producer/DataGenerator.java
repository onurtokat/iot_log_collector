package com.company.iot.data.producer;

import com.company.iot.data.model.json.Data;
import com.company.iot.data.util.JsonGenerator;
import com.company.iot.data.util.ProducerConfigCreator;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataGenerator class provides dummy iot raw data for each second
 * @author Onur Tokat
 */
public class DataGenerator implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("DataGenerator");
    private Gson gson = new Gson();
    private String topicName;

    public DataGenerator(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public void run() {
        //private ProducerRecord<String, String> producerRecord = null;
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(ProducerConfigCreator.getConfig());

        //Infinite loop for producing data
        while (true) {
            Data data = gson.fromJson(JsonGenerator.generate(), Data.class);
            kafkaProducer.send(new ProducerRecord<>(topicName, data.getDeviceId(), JsonGenerator.generate()));
            kafkaProducer.flush();
            try {
                //on each second, data will be produced
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.info("Error occurred when producing data", e);
            }
        }
    }
}
