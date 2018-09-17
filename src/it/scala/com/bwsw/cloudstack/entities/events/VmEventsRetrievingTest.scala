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
package com.bwsw.cloudstack.entities.events

import java.time.OffsetDateTime
import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.common.DefaultJsonFormats._
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmDeleteRequest}
import com.bwsw.cloudstack.entities.responses.vm.VirtualMachineCreateResponse
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class VmEventsRetrievingTest
  extends FlatSpec
    with TestEntities
    with BeforeAndAfterAll
    with Matchers {

  val serviceOfferingId: UUID = retrievedServiceOfferingId
  val templateId: UUID = retrievedTemplateId
  val zoneId: UUID = retrievedZoneId

  val sleepInterval = 10000
  val pollTimeout = 1000

  val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))

  val consumer = new Consumer(kafkaEndpoint, kafkaTopic)
  consumer.assignToEnd()

  private val beforeCreation = OffsetDateTime.now().minusSeconds(1)
  val vmId: UUID = mapper.deserialize[VirtualMachineCreateResponse](executor.executeRequest(vmCreateRequest.getRequest)).vm.id

  val vmDeleteRequest = new VmDeleteRequest(vmId)
  private val beforeDeletion = OffsetDateTime.now().minusSeconds(1)
  executor.executeRequest(vmDeleteRequest.getRequest)

  Thread.sleep(sleepInterval)

  private val records = consumer.poll(pollTimeout)
  private val events = records.map(RecordToEventDeserializer.deserializeRecord)

  it should "retrieve VirtualMachineCreateEvent with status 'Completed' from Kafka records" in {
    val afterCreation = OffsetDateTime.now()
    val actualVmCreateEvents = events.filter {
      case VirtualMachineCreateEvent(Some(Constants.Statuses.COMPLETED), Some(dateTime), _) =>
        dateTime.isAfter(beforeCreation) && dateTime.isBefore(afterCreation)
      case _ => false
    }

    actualVmCreateEvents.length should be >= 1
  }

  it should "retrieve VirtualMachineDestroyEvent with status 'Completed' from Kafka records" in {
    val afterDeletion = OffsetDateTime.now()
    val actualVmDestroyEvents = events.filter {
      case VirtualMachineDestroyEvent(Some(Constants.Statuses.COMPLETED), Some(`vmId`), Some(dateTime), _, _) =>
        dateTime.isAfter(beforeDeletion) && dateTime.isBefore(afterDeletion)
      case _ => false
    }

    actualVmDestroyEvents.length shouldBe 1
  }

  override def afterAll(): Unit = {
    consumer.close()
  }
}
