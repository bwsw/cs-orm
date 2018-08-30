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
package com.bwsw.cloudstack.entities.common

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

import com.bwsw.cloudstack.entities.events.Constants.Statuses
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.VirtualMachineDestroyEvent
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class DefaultJsonFormatsTests
  extends FlatSpec
    with Matchers {

  import DefaultJsonFormats._


  "cloudStackEventJsonReader" should "deserialize AccountCreateEvent properly" in {
    val jsonString =
      """{
        |  "Role": "b6ffb06a-c69b-11e7-bdcf-0242ac110004",
        |  "Account": "162b26e6-052a-43bb-9116-acb7555b6d7d",
        |  "eventDateTime": "2017-11-12 12:44:33 +0700",
        |  "entityuuid": "162b26e6-052a-43bb-9116-acb7555b6d7d",
        |  "description": "Successfully completed creating Account. Account Name: null, Domain Id:2",
        |  "event": "ACCOUNT.CREATE",
        |  "Domain": "16d7977e-43fd-47ad-9fff-6ef8d463516f",
        |  "user": "c1ebed36-c69b-11e7-bdcf-0242ac110004",
        |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004",
        |  "entity": "com.cloud.user.Account",
        |  "status": "Completed"
        |}""".stripMargin

    val expectedEvent = AccountCreateEvent(
      status = Some(Statuses.COMPLETED),
      entityuuid = Some(UUID.fromString("162b26e6-052a-43bb-9116-acb7555b6d7d")),
      eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 12, 44, 33, 0, ZoneOffset.ofHours(7))), //scalastyle:ignore
      domain = Some(UUID.fromString("16d7977e-43fd-47ad-9fff-6ef8d463516f")),
      description = Some("Successfully completed creating Account. Account Name: null, Domain Id:2")
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize AccountDeleteEvent properly" in {
    val jsonString =
      """{
        |  "Account": "be3bc612-30b2-4b33-93d5-afe50b3e8951",
        |  "eventDateTime": "2018-08-28 02:18:12 -0300",
        |  "entityuuid": "be3bc612-30b2-4b33-93d5-afe50b3e8951",
        |  "description": "Successfully completed deleting account. Account Id: 56",
        |  "event": "ACCOUNT.DELETE",
        |  "user": "88c34c6b-f503-11e7-a402-0242ac110003",
        |  "account": "88c31833-f503-11e7-a402-0242ac110003",
        |  "entity": "com.cloud.user.Account",
        |  "status": "Completed"
        |}""".stripMargin

    val expectedEvent = AccountDeleteEvent(
      status = Some(Statuses.COMPLETED),
      entityuuid = Some(UUID.fromString("be3bc612-30b2-4b33-93d5-afe50b3e8951")),
      eventDateTime = Some(OffsetDateTime.of(2018, 8, 28, 2, 18, 12, 0, ZoneOffset.ofHours(-3))), //scalastyle:ignore
      description = Some("Successfully completed deleting account. Account Id: 56")
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize UserCreateEvent properly" in {
    val jsonString =
      """{
        |  "Role": "b6ffb06a-c69b-11e7-bdcf-0242ac110004",
        |  "Account": "162b26e6-052a-43bb-9116-acb7555b6d7d",
        |  "eventDateTime": "2017-11-12 12:44:33 +0700",
        |  "entityuuid": "f2c54886-ab40-44ed-9ee4-27dbacd47ceb",
        |  "description": "Successfully completed creating User. Account Name: null, Domain Id:2",
        |  "event": "USER.CREATE",
        |  "Domain": "16d7977e-43fd-47ad-9fff-6ef8d463516f",
        |  "user": "c1ebed36-c69b-11e7-bdcf-0242ac110004",
        |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004",
        |  "entity": "com.cloud.user.User",
        |  "status": "Completed"
        |}""".stripMargin

    val expectedEvent = UserCreateEvent(
      status = Some(Statuses.COMPLETED),
      entityuuid = Some(UUID.fromString("f2c54886-ab40-44ed-9ee4-27dbacd47ceb")),
      eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 12, 44, 33, 0, ZoneOffset.ofHours(7))), //scalastyle:ignore
      description = Some("Successfully completed creating User. Account Name: null, Domain Id:2")
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize VirtualMachineDestroyEvent properly" in {
    val jsonString =
      """{
        |  "eventDateTime": "2017-11-12 19:44:29 +0700",
        |  "entityuuid": "3a9c10f9-0474-4173-bdbd-3f2c29a75bff",
        |  "description": "destroying vm: 14",
        |  "event": "VM.DESTROY",
        |  "user": "c1ebed36-c69b-11e7-bdcf-0242ac110004",
        |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004",
        |  "entity": "com.cloud.vm.VirtualMachine",
        |  "status": "Scheduled",
        |  "VirtualMachine": "3a9c10f9-0474-4173-bdbd-3f2c29a75bff"
        |}""".stripMargin

    val expectedEvent = VirtualMachineDestroyEvent(
      status = Some(Statuses.SCHEDULED),
      entityuuid = Some(UUID.fromString("3a9c10f9-0474-4173-bdbd-3f2c29a75bff")),
      eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 19, 44, 29, 0, ZoneOffset.ofHours(7))), //scalastyle:ignore
      description = Some("destroying vm: 14")
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize UnknownEvent properly" in {
    val jsonString =
      """{
        |  "eventDateTime": "2017-11-12 19:42:54 +0700",
        |  "zone": "d477bb3f-3592-4503-8f2a-da3d878dd476",
        |  "resource": "com.cloud.vm.VMInstanceVO$$EnhancerByCGLIB$$197e276b",
        |  "id": "3a9c10f9-0474-4173-bdbd-3f2c29a75bff",
        |  "event": "SG.ASSIGN",
        |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004"
        |}""".stripMargin

    val expectedEvent = UnknownEvent(
      JsObject(
        "eventDateTime" -> JsString("2017-11-12 19:42:54 +0700"),
        "zone" -> JsString("d477bb3f-3592-4503-8f2a-da3d878dd476"),
        "resource" -> JsString("com.cloud.vm.VMInstanceVO$$EnhancerByCGLIB$$197e276b"),
        "id" -> JsString("3a9c10f9-0474-4173-bdbd-3f2c29a75bff"),
        "event" -> JsString("SG.ASSIGN"),
        "account" -> JsString("c1ebdda3-c69b-11e7-bdcf-0242ac110004")
      )
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }
}
