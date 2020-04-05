# IoT Log Collector

Project purpose is to create data ingestion, data processing and data analysis stages for iot data which comes from three iot devices. [UPDATED! Solution document](https://github.com/onurtokat/iot_log_collector/blob/master/iot_solution_document.docx) has been added to project directory.

## Getting Started

This project have requirements:

● Task #1: Data Ingestion
  Write a simulator for IoT devices. The device will generate data according to the following template
  
  ```HTML
  {
  “data”: {
  “deviceId”: “11c1310e-c0c2-461b-a4eb-f6bf8da2d23c“,
  “temperature”: 12,
  “location”: {
  “latitude”: “52.14691120000001”,
  “longitude”: “11.658838699999933”
  },
  “time”: “1509793231”
  }
  }
  ```
  The properties are defined in the following way

Property name | Data type | Comment
--- | --- | ---
deviceId | UUID | The unique ID of the device sending the data.
temperature | Integer | The temperature measured by the device.
latitude | Long | The latitude of the position of the device.
longitude | Long | The longitude of the position of the device.
time | Timestamp | The time of the signal as a Unix timestamp.

The simulator should simulate three different devices and needs to send a signal to Apache Kafka every
second. Implement the simulator as a long running service and create Kafka topic(s) as needed.

● Task #2: Data Transformation

The data ingested in task #1 needs to be transformed into an entity and stored for long-term analysis. Create
a Spark Streaming job, which reads the data from Kafka and stores it into an HBase table. The HBase table
needs to store a data point into a single row. A row should contain the data in its raw format as well as the
entity representation of the data point. Note that the timestamp needs to be converted into human-readable
format using the pattern “yyyy-MM-dd'T'HH:mm:ssXXX”. Design and create the HBase table and
implement the Spark Streaming job.

● Task #3: Data Analysis

After the data is stored in HBase it needs to be analyzed. The data analyst responsible for the analysis
prefers to use SQL as the query language. Create an Impala table on top of the HBase table created in task2. Also, document queries for the following use cases:

1. The maximum temperatures measured for every device
2. The amount of data points aggregated for every device
3. The highest temperature measured on a given day for every device

### Prerequisites

● Cloudera Quickstart VM 5.13 has been downloaded and started up.

● Cloudera VM jdk version is set with 1.8.0_202 before the Apache Spark 2 parcel upgrade

● Apache Spark 2 parcel upgrade has been downloaded and distributed to node.

### Installing

During the first compiling of the project, gives errors which are about that some dependencies could not found. Please see [UPDATED! Solution document](https://github.com/onurtokat/iot_log_collector/blob/master/iot_solution_document.docx) for the details.

Project have two executable parts. First part is Kafka data ingestion application, the other part is Spark data processing application. Both of them have been written by Java.

```HTML
java -cp iot_log_collector-1.0-SNAPSHOT-jar-with-dependencies.jar com.company.iot.data.App
```
Before the running Spark application (spark-submit), SPARK_KAFKA_VERSION variable should be set as below

```HTML
export SPARK_KAFKA_VERSION=0.10
```

HBase table created as below
```HTML
create 'iot','rawdata','data'
```

```HTML
spark-submit --master local --class com.company.iot.data.streaming.JavaHBaseStreaming iot_log_collector-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Data Analysis Queries on Impala

1. The maximum temperatures measured for every device
```HTML
Select deviceid, max(cast(temperature as int)) from iot group by deviceid;
```
2. The amount of data points aggregated for every device
```HTML
Select deviceid, count(*) from iot group by deviceid;
```
3. The highest temperature measured on a given day for every device
```HTML
Select deviceid, from_timestamp(time,’yyyy-MM-dd’) as given_day, max(cast(temperature as int)) from iot group by deviceid,given_day;
select deviceid,max(cast(temperature as int)) from iot where time like '2020-04-04%' group by deviceid;
```

## Running the tests

Test classes can found under src/test/java/com.company.iot.data.util/ directory.

```HTML
JsonGeneratorTest.class
ProducerConfigCreatorTest.class
```
### Break down into end to end tests

<li>JsonGenerator generate method correctness has been checked (Valid JSON format)</li>
<li>Kafka Producer Config generation correctness has been checked</li>  

## Deployment

 This project can be run as JAR in the;
 
 <li>Locally for testing</li>
 <li>On a server</li>
 <li>On a server hosted by cloud provider</li>
 <li>In a container</li>
 <li>In a container hosted by cloud provider</li>

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Apache Kafka](https://kafka.apache.org/) - Kafka Producer API
* [Apache Spark](https://spark.apache.org/) - Spark Streaming API
* [Apache HBase](https://hbase.apache.org/) - HBase Configuration
* [Apache Hadoop](https://hadoop.apache.org/) - Hadoop File System Configuration
* [Scala](https://www.scala-lang.org/) - Scala language
 

## Authors

Onur Tokat

## Limitations

* Hortonworks shc connector have bug which is java.lang.NoSuchMethodError: org.json4s.jackson.JsonMethods$.parse
