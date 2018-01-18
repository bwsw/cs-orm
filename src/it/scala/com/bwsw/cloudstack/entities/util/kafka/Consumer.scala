/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package com.bwsw.cloudstack.entities.util.kafka

import java.util.{Collections, Properties, UUID}

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConverters._

class Consumer(brokers: String, topic: String) {
  private val groupId = "testGroup"
  private val props = createConsumerConfig()
  protected val consumer: org.apache.kafka.clients.consumer.Consumer[String, String] = new KafkaConsumer[String, String](props)

  private def createConsumerConfig(): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "2000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }

  def close(): Unit = {
    consumer.close()
  }

  def poll(timeout: Int): List[String] = {
    val records = consumer.poll(timeout)
    records.asScala.map(_.value()).toList
  }

  def assignToEnd(): Unit = {
    val partitions = consumer.partitionsFor(topic).asScala.map(x => new TopicPartition(topic, x.partition())).asJavaCollection
    consumer.assign(partitions)
    consumer.endOffsets(partitions).asScala.foreach {
      case (partition, offset) => consumer.seek(partition, offset)
    }
  }
}
