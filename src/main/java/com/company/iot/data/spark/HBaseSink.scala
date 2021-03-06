package com.company.iot.data.spark

import org.apache.hadoop.hbase.spark.datasources.HBaseTableCatalog
import org.apache.spark.internal.Logging
import org.apache.spark.sql.execution.streaming.Sink
import org.apache.spark.sql.{DataFrame}

class HBaseSink(options: Map[String, String]) extends Sink with Logging {
  // String with HBaseTableCatalog.tableCatalog
  private val hBaseCatalog = options.get("hbasecat").map(_.toString).getOrElse("")

  override def addBatch(batchId: Long, data: DataFrame): Unit = synchronized {
    val df = data.sparkSession.createDataFrame(data.rdd, data.schema)
    df.write
      .options(Map(HBaseTableCatalog.tableCatalog->hBaseCatalog,
        HBaseTableCatalog.newTable -> "5"))
      .format("org.apache.spark.sql.execution.datasources.hbase").save()
  }
}
