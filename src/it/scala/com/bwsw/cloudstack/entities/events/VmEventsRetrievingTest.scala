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

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmDeleteRequest}
import com.bwsw.cloudstack.entities.responses.vm.VirtualMachineCreateResponse
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class VmEventsRetrievingTest extends FlatSpec with TestEntities with BeforeAndAfterAll {
  val serviceOfferingId = retrievedServiceOfferingId
  val templateId = retrievedTemplateId
  val zoneId = retrievedZoneId

  val sleepInterval = 15000
  val pollTimeout = 1000

  val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))

  val consumer = new Consumer(kafkaEndpoint, kafkaTopic)
  consumer.assignToEnd()

  val vmId = mapper.deserialize[VirtualMachineCreateResponse](executor.executeRequest(vmCreateRequest.request)).vm.id
  val vmDeleteRequest = new VmDeleteRequest(vmId)
  executor.executeRequest(vmDeleteRequest.request)

  Thread.sleep(sleepInterval)

  val records = consumer.poll(pollTimeout)

  it should "retrieve VirtualMachineCreateEvent with status 'Completed' from Kafka records" in {
    val expectedVmCreateEvents = List(VirtualMachineCreateEvent(Some(Constants.Statuses.COMPLETED), Some(vmId)))

    val actualVmCreateEvents = records.map(x => RecordToEventDeserializer.deserializeRecord(x, mapper)).filter {
      case VirtualMachineCreateEvent(Some(status), Some(entityId))
        if status == Constants.Statuses.COMPLETED && entityId == vmId => true
      case _ => false
    }

    assert(actualVmCreateEvents == expectedVmCreateEvents, s"records count: ${records.size}")
  }

  it should "retrieve VirtualMachineDestroyEvent with status 'Completed' from Kafka records" in {
    val expectedVmDestroyEvents = List(VirtualMachineDestroyEvent(Some(Constants.Statuses.COMPLETED), Some(vmId)))

    val actualVmDestroyEvents = records.map(x => RecordToEventDeserializer.deserializeRecord(x, mapper)).filter {
      case VirtualMachineDestroyEvent(Some(status), Some(entityId))
        if status == Constants.Statuses.COMPLETED && entityId == vmId => true
      case _ => false
    }

    assert(actualVmDestroyEvents == expectedVmDestroyEvents, s"records count: ${records.size}")
  }

  override def afterAll(): Unit = {
    consumer.close()
  }
}
