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

import java.time.{OffsetDateTime, ZoneOffset}
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
    //scalastyle:off
    val eventDateTime = OffsetDateTime.MIN
      .withYear(2011)
      .withMonth(1)
      .withDayOfMonth(1)
      .withHour(1)
      .withMinute(1)
      .withSecond(1)
      .withOffsetSameLocal(ZoneOffset.UTC)
    //scalastyle:on
    val userCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.USER_CREATE}",
         |  "entityuuid": "$userId",
         |  "eventDateTime": "2011-01-01 01:01:01Z"
         |}""".stripMargin

    val expectedEvent = UserCreateEvent(status, userId, eventDateTime)

    userCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the AccoutCreateEvent if event is ${Events.ACCOUNT_CREATE}" in {
    val accountId = UUID.randomUUID()
    //scalastyle:off
    val eventDateTime = OffsetDateTime.MIN
      .withYear(2018)
      .withMonth(3)
      .withDayOfMonth(23)
      .withHour(9)
      .withMinute(4)
      .withSecond(1)
      .withOffsetSameLocal(ZoneOffset.ofHours(-4))
    //scalastyle:on
    val accountCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.ACCOUNT_CREATE}",
         |  "entityuuid": "$accountId",
         |  "eventDateTime": "2018-03-23T09:04:01 -0400"
         |}""".stripMargin

    val expectedEvent = AccountCreateEvent(status, accountId, eventDateTime)

    accountCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the AccoutDeleteEvent if event is ${Events.ACCOUNT_DELETE}" in {
    val accountId = UUID.randomUUID()
    //scalastyle:off
    val eventDateTime = OffsetDateTime.MIN
      .withYear(2017)
      .withMonth(11)
      .withDayOfMonth(12)
      .withHour(13)
      .withMinute(36)
      .withSecond(23)
      .withOffsetSameLocal(ZoneOffset.ofHours(7))
    //scalastyle:on
    val accountDeleteEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.ACCOUNT_DELETE}",
         |  "entityuuid": "$accountId",
         |  "eventDateTime": "2017-11-12 13:36:23 +0700"
         |}""".stripMargin

    val expectedEvent = AccountDeleteEvent(status, accountId, eventDateTime)

    accountDeleteEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the VirtualMachineCreateEvent if event is ${Events.VM_CREATE}" in {
    val vmId = UUID.randomUUID()
    //scalastyle:off
    val eventDateTime = OffsetDateTime.MIN
      .withYear(2017)
      .withMonth(11)
      .withDayOfMonth(12)
      .withHour(13)
      .withMinute(36)
      .withSecond(23)
      .withOffsetSameLocal(ZoneOffset.ofHours(7))
    //scalastyle:on
    val vmCreateEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.VM_CREATE}",
         |  "entityuuid": "$vmId",
         |  "eventDateTime": "2017-11-12 13:36:23 +0700"
         |}""".stripMargin

    val expectedEvent = VirtualMachineCreateEvent(status, vmId, eventDateTime)

    vmCreateEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }

  it should s"be deserialized to the VirtualMachineDestroyEvent if event is ${Events.VM_DESTROY}" in {
    val vmId = UUID.randomUUID()
    //scalastyle:off
    val eventDateTime = OffsetDateTime.MIN
      .withYear(2017)
      .withMonth(11)
      .withDayOfMonth(12)
      .withHour(13)
      .withMinute(36)
      .withSecond(23)
      .withOffsetSameLocal(ZoneOffset.ofHours(7))
    //scalastyle:on
    val vmDestroyEventJson =
      s"""{
         |  "status": "$status",
         |  "event": "${Events.VM_DESTROY}",
         |  "entityuuid": "$vmId",
         |  "eventDateTime": "2017-11-12 13:36:23 +0700"
         |}""".stripMargin

    val expectedEvent = VirtualMachineDestroyEvent(status, vmId, eventDateTime)

    vmDestroyEventJson.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }
}
