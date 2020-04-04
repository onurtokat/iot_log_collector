package com.company.iot.data.streaming;

import com.company.iot.data.model.json.Data;
import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JavaHBaseStreaming class provides spark streaming application
 * from Kafka topic to HBase table.
 * @author Onur Tokat
 */
public class JavaHBaseStreaming {

    private static final Logger LOGGER = LoggerFactory.getLogger("JavaHBaseStreaming");

    private static Gson gson = new Gson();
    private static final String TABLE_NAME = "iot";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private JavaHBaseStreaming() {
    }

    public static void main(String[] args) {

        SparkConf sparkConf =
                new SparkConf().setMaster("local[*]").setAppName(UUID.randomUUID().toString());
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        //Kafka configuration
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Collections.singletonList("device1");

        try {
            JavaStreamingContext jssc =
                    new JavaStreamingContext(jsc, new Duration(1000));

            JavaInputDStream<ConsumerRecord<String, String>> stream =
                    KafkaUtils.createDirectStream(
                            jssc,
                            LocationStrategies.PreferConsistent(),
                            ConsumerStrategies.Subscribe(topics, kafkaParams)
                    );

            JavaDStream<String> javaDstream = stream.map(ConsumerRecord::value);

            //HBase configuration
            Configuration conf = HBaseConfiguration.create();
            conf.addResource(new Path("/etc/hadoop/conf.cloudera.hdfs/core-site.xml"));
            conf.addResource(new Path("/etc/hbase/conf.cloudera.hbase/hbase-site.xml"));

            JavaHBaseContext hbaseContext = new JavaHBaseContext(jsc, conf);

            hbaseContext.streamBulkPut(javaDstream,
                    TableName.valueOf(TABLE_NAME),
                    new PutFunction());
            jssc.start();
            try {
                jssc.awaitTermination();
            } catch (InterruptedException e) {
                LOGGER.error("Error occurred when java spark streaming context operation: ",e);
            }
        } finally {
            jsc.stop();
        }
    }

    public static class PutFunction implements Function<String, Put> {

        private static final long serialVersionUID = 1L;
        public Put call(String v) {
            Data data = gson.fromJson(v, Data.class);

            //composite rowkey with deviceId and time
            Put put = new Put(Bytes.toBytes(data.getDeviceId() + "_" + data.getTime()));
            put.addColumn(Bytes.toBytes("rawdata"), Bytes.toBytes("rawValue"), Bytes.toBytes(v));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("deviceId"), Bytes.toBytes(data.getDeviceId()));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("temperature"), Bytes.toBytes(String.valueOf(data.getTemperature())));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("latitude"), Bytes.toBytes(String.valueOf(data.getLocation().getLatitude())));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("longitude"), Bytes.toBytes(String.valueOf(data.getLocation().getLongitude())));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("time"), Bytes.toBytes(getFormattedDateTime(data.getTime())));
            return put;
        }
    }

    /**
     *getFormattedDateTime method
     * @param value long value
     * @return String of formatted local date time
     */
    private static String getFormattedDateTime(Long value) {
        Instant instant = Instant.ofEpochSecond(value);
        return (LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()).format(formatter));
    }
}
