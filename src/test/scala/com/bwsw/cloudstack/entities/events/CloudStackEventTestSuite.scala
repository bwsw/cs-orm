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

import com.bwsw.cloudstack.entities.common.JsonFormats._
import com.bwsw.cloudstack.entities.events.Constants._
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class CloudStackEventTestSuite extends FlatSpec with Matchers {

  val status: String = Statuses.COMPLETED

  it should s"be deserialized to the UserCreateEvent if event is ${Events.USER_CREATE}" in {
    val userId = UUID.randomUUID()
    val userCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.USER_CREATE}",
         |  "entityuuid": "$userId"
         |}""".stripMargin

    val expectedEvent = UserCreateEvent(status, userId)

    userCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the AccoutCreateEvent if event is ${Events.ACCOUNT_CREATE}" in {
    val accountId = UUID.randomUUID()
    val accountCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.ACCOUNT_CREATE}",
         |  "entityuuid": "$accountId"
         |}""".stripMargin

    val expectedEvent = AccountCreateEvent(status, accountId)

    accountCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the AccoutDeleteEvent if event is ${Events.ACCOUNT_DELETE}" in {
    val accountId = UUID.randomUUID()
    val accountDeleteEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.ACCOUNT_DELETE}",
         |  "entityuuid": "$accountId"
         |}""".stripMargin

    val expectedEvent = AccountDeleteEvent(status, accountId)

    accountDeleteEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the VirtualMachineCreateEvent if event is ${Events.VM_CREATE}" in {
    val vmId = UUID.randomUUID()
    val vmCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.VM_CREATE}",
         |  "entityuuid": "$vmId"
         |}""".stripMargin

    val expectedEvent = VirtualMachineCreateEvent(status, vmId)

    vmCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the VirtualMachineDestroyEvent if event is ${Events.VM_DESTROY}" in {
    val vmId = UUID.randomUUID()
    val vmDestroyEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.VM_DESTROY}",
         |  "entityuuid": "$vmId"
         |}""".stripMargin

    val expectedEvent = VirtualMachineDestroyEvent(status, vmId)

    vmDestroyEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }
}
