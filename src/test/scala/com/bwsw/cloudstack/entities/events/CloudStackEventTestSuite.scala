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

import com.bwsw.cloudstack.entities.common.JsonMapper
import com.bwsw.cloudstack.entities.events.Constants._
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import org.scalatest.FlatSpec

class CloudStackEventTestSuite extends FlatSpec {
  val status = Statuses.COMPLETE

  it should "be deserialized to the UserCreateEvent if event is USER.CREATE" in {
    val userId = UUID.randomUUID()
    val userCreateEventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"${Events.USER_CREATE}" + "\",\"entityuuid\":\"" + s"$userId" + "\"}"

    val expectedEvent = UserCreateEvent(Some(status), Some(userId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](userCreateEventJson) == expectedEvent)
  }

  it should "be deserialized to the AccoutCreateEvent if event is ACCOUNT.CREATE" in {
    val accountId = UUID.randomUUID()
    val accountCreateEventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"${Events.ACCOUNT_CREATE}" + "\",\"entityuuid\":\"" + s"$accountId" + "\"}"

    val expectedEvent = AccountCreateEvent(Some(status), Some(accountId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](accountCreateEventJson) == expectedEvent)
  }

  it should "be deserialized to the AccoutDeleteEvent if event is ACCOUNT.DELETE" in {
    val accountId = UUID.randomUUID()
    val accountDeleteEventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"${Events.ACCOUNT_DELETE}" + "\",\"entityuuid\":\"" + s"$accountId" + "\"}"

    val expectedEvent = AccountDeleteEvent(Some(status), Some(accountId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](accountDeleteEventJson) == expectedEvent)
  }

  it should "be deserialized to the VirtualMachineCreateEvent if event is VM.CREATE" in {
    val vmId = UUID.randomUUID()
    val vmCreateEventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"${Events.VM_CREATE}" + "\",\"entityuuid\":\"" + s"$vmId" + "\"}"

    val expectedEvent = VirtualMachineCreateEvent(Some(status), Some(vmId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](vmCreateEventJson) == expectedEvent)
  }

  it should "be deserialized to the VirtualMachineDestroyEvent if event is VM.DESTROY" in {
    val vmId = UUID.randomUUID()
    val vmDestroyEventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"${Events.VM_DESTROY}" + "\",\"entityuuid\":\"" + s"$vmId" + "\"}"

    val expectedEvent = VirtualMachineDestroyEvent(Some(status), Some(vmId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](vmDestroyEventJson) == expectedEvent)
  }


  it should "be deserialized to the CloudStackEvent if event value is not described in JsonSubTypes annotation" in {
    val entityId = UUID.randomUUID()
    val event = UUID.randomUUID().toString
    val eventJson = "{\"status\":\"" + status + "\",\"event\":\"" + s"$event" + "\",\"entityuuid\":\"" + s"$entityId" + "\"}"

    val expectedEvent = new CloudStackEvent(Some(status), Some(event), Some(entityId))
    val mapper = new JsonMapper(true)

    assert(mapper.deserialize[CloudStackEvent](eventJson).getClass == expectedEvent.getClass)
  }
}
