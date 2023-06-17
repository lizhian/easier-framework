package tydic.framework.test

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.slf4j.LoggerFactory

class Flink {

}

fun main() {
    val root = LoggerFactory.getLogger("root")
    if (root is Logger) {
        root.level = Level.INFO
    }
    val streamEnv = StreamExecutionEnvironment.getExecutionEnvironment()
    streamEnv.parallelism = 1
    val tableEnv = StreamTableEnvironment.create(streamEnv)
    tableEnv.executeSql(
        """
        CREATE TABLE KafkaTable (
          id ROW<id STRING,name STRING,junbiaoID STRING,forceCamp STRING,SystemID STRING>,
          parentID STRING,
          velocity ROW<x STRING,y STRING,z STRING>,
          guardDir ROW<x STRING,y STRING,z STRING>,
          healthStatus STRING,
          WeaponeSize STRING,
          containerID STRING,
          isInContainer STRING,
          curWeapone STRING,
          arrays ARRAY<ROW<x STRING,y STRING,z STRING>>,
          ts TIMESTAMP(3) METADATA FROM 'timestamp'
        ) WITH (
          'connector' = 'kafka',
          'topic' = 'flink-test',
          'properties.bootstrap.servers' = '192.168.2.182:9092',
          'properties.group.id' = 'flink-test',
          'scan.startup.mode' = 'earliest-offset',
          'format' = 'json'
        )
        """.trimIndent()
    )

    /*val sqlQuery = tableEnv.sqlQuery(
        """
        select * from KafkaTable
        """.trimIndent()
    )*/


    val sqlQuery = tableEnv.sqlQuery(
        """
        select 
        id.id,
        id.name,
        id.junbiaoID,
        velocity.x as px,
        velocity.y as py,
        velocity.z as pz,
        arrays[1].x as ax
        from KafkaTable
        """
    )

    sqlQuery.execute().print();
    streamEnv.execute("Flink SQL Kafka Demo");
}