package com.company.iot.data.spark

import java.util.UUID

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}

/**
 * @author Onur Tokat
 *         ScalaStreamingApp provides streaming application
 *         using SparkSession object for using SparkSQL API.
 *         Hortonworks shc API is used as HBase connector.
 *         //FIXME current API have bug which is java.lang.NoSuchMethodError: org.json4s.jackson.JsonMethods$.parse
 *         //FIXME DO NOT RUN THIS CLASS! IT IS JUST TESTING PURPOSE
 */
object ScalaStreamingApp {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    if (args.length != 1) {
      println("Topic name should be entered as argument")
      System.exit(-1)
    }

    val spark = SparkSession.builder().appName(UUID.randomUUID().toString)
      .config("spark.sql.streaming.unsupportedOperationCheck", false)
      .getOrCreate()

    // creating stream DataFrame from Kafka Topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", args(0))
      .option("zookeeper.connect", "localhost:2181")
      .option("startingOffsets", "earliest")
      .option("max.poll.records", 10)
      .option("failOnDataLoss", false)
      .load()

    df.printSchema()

    //creating schema for iot data
    val dataSchema = StructType(List(
      StructField("deviceId", StringType, true),
      StructField("temperature", IntegerType),
      StructField("location",
        StructType(List(
          StructField("latitude", LongType),
          StructField("longitude", LongType)))),
      StructField("time", LongType)
    ))

    //creating schema for HBase data
    def catalog =
      s"""{
         |"table":{"namespace":"default", "name":"iot"},
         |"rowkey":"deviceId",
         |"columns":{
         |"deviceId":{"cf":"rowkey", "col":"deviceId", "type":"string"},
         |"value":{"cf":"rawdata", "col":"rawValue", "type":"string"},
         |"deviceId":{"cf":"data", "col":"deviceId", "type":"string"},
         |"deviceId":{"cf":"data", "col":"temperature", "type":"integer"},
         |"latitude":{"cf":"data", "col":"latitude", "type":"long"},
         |"longitude":{"cf":"data", "col":"longitude", "type":"long"},
         |"time":{"cf":"data", "col":"time", "type":"long"}
         |}
         |}""".stripMargin

    import org.apache.spark.sql.functions._

    //transformation on raw data
    val formattedDF = df.withColumn("formatted", from_json(col("value").cast(StringType), dataSchema))
      .withColumn("rawValue", col("value").cast(StringType))
      .select("rawValue", "formatted.deviceId", "formatted.temperature",
        "formatted.location.latitude", "formatted.location.longitude", "formatted.time")
      .withColumn("time", from_unixtime(col("time").cast(LongType), "yyyy-MM-dd'T'HH:mm:ssXXX"))

    formattedDF.printSchema()

    //write data to HBase
    val streaming = formattedDF.writeStream
      .queryName("hbase writer")
      .format("com.company.iot.data.spark.HBaseSinkProvider")
      .option("checkpointLocation", "/user/cloudera/checkpoint_onur")
      .option("hbasecat", catalog)
      .outputMode(OutputMode.Update())
      .trigger(Trigger.ProcessingTime("30 seconds"))
      .start

    streaming.awaitTermination()

    spark.stop()
  }
}
