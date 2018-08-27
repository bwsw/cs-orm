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

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmDeleteRequest}
import com.bwsw.cloudstack.entities.responses.vm.VirtualMachineCreateResponse
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class VmEventsRetrievingTest
  extends FlatSpec
    with TestEntities
    with BeforeAndAfterAll {

  val serviceOfferingId: UUID = retrievedServiceOfferingId
  val templateId: UUID = retrievedTemplateId
  val zoneId: UUID = retrievedZoneId

  val sleepInterval = 15000
  val pollTimeout = 1000

  val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))

  val consumer = new Consumer(kafkaEndpoint, kafkaTopic)
  consumer.assignToEnd()

  val vmId: UUID = mapper.deserialize[VirtualMachineCreateResponse](executor.executeRequest(vmCreateRequest.getRequest)).vm.id
  val vmDeleteRequest = new VmDeleteRequest(vmId)
  executor.executeRequest(vmDeleteRequest.getRequest)

  Thread.sleep(sleepInterval)

  val records: List[String] = consumer.poll(pollTimeout)

  it should "retrieve VirtualMachineCreateEvent with status 'Completed' from Kafka records" in {
    val expectedVmCreateEvents = List(VirtualMachineCreateEvent(Constants.Statuses.COMPLETED, vmId))

    val actualVmCreateEvents = records.map(RecordToEventDeserializer.deserializeRecord).filter {
      case VirtualMachineCreateEvent(Constants.Statuses.COMPLETED, `vmId`) => true
      case _ => false
    }

    assert(actualVmCreateEvents == expectedVmCreateEvents, s"records count: ${records.size}")
  }

  it should "retrieve VirtualMachineDestroyEvent with status 'Completed' from Kafka records" in {
    val expectedVmDestroyEvents = List(VirtualMachineDestroyEvent(Constants.Statuses.COMPLETED, vmId))

    val actualVmDestroyEvents = records.map(RecordToEventDeserializer.deserializeRecord).filter {
      case VirtualMachineDestroyEvent(Constants.Statuses.COMPLETED, `vmId`) => true
      case _ => false
    }

    assert(actualVmDestroyEvents == expectedVmDestroyEvents, s"records count: ${records.size}")
  }

  override def afterAll(): Unit = {
    consumer.close()
  }
}
