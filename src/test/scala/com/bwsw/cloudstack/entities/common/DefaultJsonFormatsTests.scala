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
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class DefaultJsonFormatsTests
  extends FlatSpec
    with Matchers
    with TableDrivenPropertyChecks {

  import DefaultJsonFormats._

  "cloudStackEventJsonReader" should "deserialize events properly" in {
    val events = Table(
      ("json", "event"),
      (
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
          |}""".stripMargin,
        AccountCreateEvent(
          status = Some(Statuses.COMPLETED),
          entityuuid = UUID.fromString("162b26e6-052a-43bb-9116-acb7555b6d7d"),
          eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 12, 44, 33, 0, ZoneOffset.ofHours(7))) //scalastyle:ignore
        )
      ),
      (
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
          |}""".stripMargin,
        AccountDeleteEvent(
          Some(Statuses.COMPLETED),
          UUID.fromString("be3bc612-30b2-4b33-93d5-afe50b3e8951"),
          Some(OffsetDateTime.of(2018, 8, 28, 2, 18, 12, 0, ZoneOffset.ofHours(-3))) //scalastyle:ignore
        )
      ),
      (
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
          |}""".stripMargin,
        UserCreateEvent(
          status = Some(Statuses.COMPLETED),
          entityuuid = UUID.fromString("f2c54886-ab40-44ed-9ee4-27dbacd47ceb"),
          eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 12, 44, 33, 0, ZoneOffset.ofHours(7))) //scalastyle:ignore
        )
      ),
      (
        """{
          |  "eventDateTime": "2017-11-12 13:36:23 +0700",
          |  "VirtualMachineTemplate": "aaa5faf7-7680-4393-aee5-c9997b3420cd",
          |  "ServiceOffering": "b1196c0e-0c1a-4416-bea8-f6a62309fac5",
          |  "description": "starting Vm. Vm Id: 12",
          |  "SecurityGroup": "86036515-1f1c-4a48-92ac-519ef0e22912",
          |  "DataCenter": "d477bb3f-3592-4503-8f2a-da3d878dd476",
          |  "entityuuid": "63f55cf9-2d1b-42f0-9202-a784af1d39ed",
          |  "event": "VM.CREATE",
          |  "user": "c1ebed36-c69b-11e7-bdcf-0242ac110004",
          |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004",
          |  "entity": "com.cloud.vm.VirtualMachine",
          |  "status": "Scheduled",
          |  "VirtualMachine": "63f55cf9-2d1b-42f0-9202-a784af1d39ed"
          |}""".stripMargin,
        VirtualMachineCreateEvent(
          status = Some(Statuses.SCHEDULED),
          entityuuid = UUID.fromString("63f55cf9-2d1b-42f0-9202-a784af1d39ed"),
          eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 13, 36, 23, 0, ZoneOffset.ofHours(7))) //scalastyle:ignore
        )
      ),
      (
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
          |}""".stripMargin,
        VirtualMachineDestroyEvent(
          status = Some(Statuses.SCHEDULED),
          entityuuid = UUID.fromString("3a9c10f9-0474-4173-bdbd-3f2c29a75bff"),
          eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 19, 44, 29, 0, ZoneOffset.ofHours(7))) //scalastyle:ignore
        )
      ),
      (
        """{
          |  "eventDateTime": "2017-11-12 19:42:54 +0700",
          |  "zone": "d477bb3f-3592-4503-8f2a-da3d878dd476",
          |  "resource": "com.cloud.vm.VMInstanceVO$$EnhancerByCGLIB$$197e276b",
          |  "id": "3a9c10f9-0474-4173-bdbd-3f2c29a75bff",
          |  "event": "SG.ASSIGN",
          |  "account": "c1ebdda3-c69b-11e7-bdcf-0242ac110004"
          |}""".stripMargin,
        UnknownEvent(
          JsObject(
            "eventDateTime" -> JsString("2017-11-12 19:42:54 +0700"),
            "zone" -> JsString("d477bb3f-3592-4503-8f2a-da3d878dd476"),
            "resource" -> JsString("com.cloud.vm.VMInstanceVO$$EnhancerByCGLIB$$197e276b"),
            "id" -> JsString("3a9c10f9-0474-4173-bdbd-3f2c29a75bff"),
            "event" -> JsString("SG.ASSIGN"),
            "account" -> JsString("c1ebdda3-c69b-11e7-bdcf-0242ac110004")
          )
        )
      )
    )

    forAll(events) { (json, event) =>
      json.parseJson.convertTo[CloudStackEvent] shouldBe event
    }
  }

}
