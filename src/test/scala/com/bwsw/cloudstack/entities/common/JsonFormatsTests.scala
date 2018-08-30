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
import com.bwsw.cloudstack.entities.events.vm.VirtualMachineCreateEvent
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class JsonFormatsTests
  extends FlatSpec
    with Matchers
    with TableDrivenPropertyChecks
    with JsonFormats {


  "jsonToCloudStackEvent" should "build deserializer properly" in {
    case object ObjectEvent extends CloudStackEvent
    case object BigArrayEvent extends CloudStackEvent
    case object SmallArrayEvent extends CloudStackEvent

    implicit val cloudStackEventJsonReader: RootJsonReader[CloudStackEvent] = jsonToCloudStackEvent(
      jsObjectParser = {
        case _: JsObject => ObjectEvent
      },
      otherParsers = Seq(
        { case JsArray(array) if array.length > 2 => BigArrayEvent },
        { case _: JsArray => SmallArrayEvent }
      )
    )
    val events = Table(
      ("json", "event"),
      ( """{"f1":"v1","f2":"v2"}""", ObjectEvent),
      ( """[{"f1":"v1"},{"f2":"v2"}]""", SmallArrayEvent),
      ( """[{"f1":"v1"},{"f2":"v2"},{"f3":"v3"}]""", BigArrayEvent),
      ( """false""", UnknownEvent(JsFalse))
    )

    forAll(events) { (json, event) =>
      json.parseJson.convertTo[CloudStackEvent] shouldBe event
    }
  }


  "jsObjectToCloudStackEvent" should "build parser for JsObject properly" in {
    case object CustomEvent extends CloudStackEvent
    case object EventWithFieldDescription extends CloudStackEvent

    val parser = jsObjectToCloudStackEvent(
      typedEvents = {
        case ("custom", _) =>
          (_: JsValue) => CustomEvent
      },
      untypedEvents = {
        case JsObject(fields) if fields.isDefinedAt("description") =>
          (_: JsValue) => EventWithFieldDescription
      }
    )

    implicit val cloudStackEventJsonReader: RootJsonReader[CloudStackEvent] = (json: JsValue) => parser(json)

    val events = Table(
      ("json", "event"),
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
          entityuuid = Some(UUID.fromString("63f55cf9-2d1b-42f0-9202-a784af1d39ed")),
          eventDateTime = Some(OffsetDateTime.of(2017, 11, 12, 13, 36, 23, 0, ZoneOffset.ofHours(7))), //scalastyle:ignore
          description = Some("starting Vm. Vm Id: 12")
        )
      ),
      ("""{"event": "custom"}""", CustomEvent),
      ("""{"description": "some description"}""", EventWithFieldDescription),
      ("""{"unknown": "event"}""", UnknownEvent(JsObject("unknown" -> JsString("event"))))
    )

    forAll(events) { (json, event) =>
      json.parseJson.convertTo[CloudStackEvent] shouldBe event
    }
  }

}
