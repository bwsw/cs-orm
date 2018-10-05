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

import java.util.UUID

import com.bwsw.cloudstack.entities.events.Constants.Statuses
import com.bwsw.cloudstack.entities.events.jobresults.{NetworkInterface, VirtualMachineJobResult}
import com.bwsw.cloudstack.entities.events.vm.VirtualMachineCreateEvent
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, JobResult, UnknownEvent}
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
          |  "jobId": "2c9bd9c7-11e1-4354-abb5-2c619fbb4589",
          |  "commandEventType": "VM.CREATE",
          |  "processStatus": "0",
          |  "cmdInfo": "{\"iptonetworklist[0].networkid\":\"95b8b9cf-fd93-4601-b037-915b2bf5d5a2\",\"httpmethod\":\"GET\",\"templateid\":\"9b5a23d5-f503-11e7-a402-0242ac110003\",\"ctxAccountId\":\"37\",\"uuid\":\"991558d9-5dfe-4272-85aa-98cbf7c10830\",\"cmdEventType\":\"VM.CREATE\",\"serviceofferingid\":\"891b5344-83c6-453a-8521-1cdfa17f920b\",\"response\":\"json\",\"ctxUserId\":\"37\",\"hypervisor\":\"Simulator\",\"iptonetworklist[1].networkid\":\"a72d1fb8-c754-42f2-8bad-295084c4f981\",\"zoneid\":\"9f5c2d2a-05ac-476d-b274-f58cc211b007\",\"ctxStartEventId\":\"1873\",\"id\":\"36\",\"ctxDetails\":\"{\\\"interface com.cloud.vm.VirtualMachine\\\":\\\"991558d9-5dfe-4272-85aa-98cbf7c10830\\\",\\\"interface com.cloud.template.VirtualMachineTemplate\\\":\\\"9b5a23d5-f503-11e7-a402-0242ac110003\\\",\\\"interface com.cloud.dc.DataCenter\\\":\\\"9f5c2d2a-05ac-476d-b274-f58cc211b007\\\",\\\"interface com.cloud.offering.ServiceOffering\\\":\\\"891b5344-83c6-453a-8521-1cdfa17f920b\\\"}\",\"_\":\"1537859651513\"}",
          |  "instanceType": "VirtualMachine",
          |  "jobResult": "org.apache.cloudstack.api.response.UserVmResponse/virtualmachine/{\"id\":\"991558d9-5dfe-4272-85aa-98cbf7c10830\",\"name\":\"VM-991558d9-5dfe-4272-85aa-98cbf7c10830\",\"displayname\":\"VM-991558d9-5dfe-4272-85aa-98cbf7c10830\",\"account\":\"aaa\",\"userid\":\"9e476079-af42-4a55-9880-e9811595a104\",\"username\":\"aaa\",\"domainid\":\"bcaf64de-56ff-44ef-9f4a-d5959cb3d63b\",\"domain\":\"aaa\",\"created\":\"2018-09-25T07:14:11+0000\",\"state\":\"Running\",\"haenable\":false,\"zoneid\":\"9f5c2d2a-05ac-476d-b274-f58cc211b007\",\"zonename\":\"Sandbox-simulator-advanced\",\"templateid\":\"9b5a23d5-f503-11e7-a402-0242ac110003\",\"templatename\":\"CentOS 5.6 (64-bit) no GUI (Simulator)\",\"templatedisplaytext\":\"CentOS 5.6 (64-bit) no GUI (Simulator)\",\"passwordenabled\":false,\"serviceofferingid\":\"891b5344-83c6-453a-8521-1cdfa17f920b\",\"serviceofferingname\":\"Small Instance\",\"cpunumber\":1,\"cpuspeed\":500,\"memory\":512,\"guestosid\":\"88acb71a-f503-11e7-a402-0242ac110003\",\"rootdeviceid\":0,\"rootdevicetype\":\"ROOT\",\"securitygroup\":[],\"nic\":[{\"id\":\"674aeb81-c153-4b39-b462-85d47c304efc\",\"networkid\":\"95b8b9cf-fd93-4601-b037-915b2bf5d5a2\",\"networkname\":\"ggg\",\"netmask\":\"255.255.255.0\",\"gateway\":\"10.1.1.1\",\"ipaddress\":\"10.1.1.19\",\"isolationuri\":\"vlan://103\",\"broadcasturi\":\"vlan://103\",\"traffictype\":\"Guest\",\"type\":\"Isolated\",\"isdefault\":true,\"macaddress\":\"02:00:0f:71:00:01\",\"secondaryip\":[]},{\"id\":\"ace7cb25-6b7a-4315-b519-59d1fde5666f\",\"networkid\":\"a72d1fb8-c754-42f2-8bad-295084c4f981\",\"networkname\":\"hhhh\",\"netmask\":\"255.255.255.0\",\"gateway\":\"10.1.1.1\",\"ipaddress\":\"10.1.1.109\",\"isolationuri\":\"vlan://197\",\"broadcasturi\":\"vlan://197\",\"traffictype\":\"Guest\",\"type\":\"Isolated\",\"isdefault\":false,\"macaddress\":\"02:00:45:47:00:01\",\"secondaryip\":[]}],\"hypervisor\":\"Simulator\",\"details\":{},\"affinitygroup\":[],\"displayvm\":true,\"isdynamicallyscalable\":false,\"ostypeid\":142,\"tags\":[],\"jobid\":\"2c9bd9c7-11e1-4354-abb5-2c619fbb4589\",\"jobstatus\":0}",
          |  "resultCode": "0",
          |  "instanceUuid": "991558d9-5dfe-4272-85aa-98cbf7c10830",
          |  "user": "9e476079-af42-4a55-9880-e9811595a104",
          |  "command": "org.apache.cloudstack.api.command.user.vm.DeployVMCmd",
          |  "account": "67d724b9-d104-4f5c-ba1c-a2aa3d39632f",
          |  "status": "SUCCEEDED"
          |}
        """.stripMargin,
        VirtualMachineCreateEvent(
          status = Some(Statuses.SUCCEEDED),
          eventDateTime = None,
          jobResult = Some(JobResult("org.apache.cloudstack.api.response.UserVmResponse/virtualmachine/",
            Right(
              VirtualMachineJobResult(
                id = UUID.fromString("991558d9-5dfe-4272-85aa-98cbf7c10830"),
                account = "aaa",
                domainId = UUID.fromString("bcaf64de-56ff-44ef-9f4a-d5959cb3d63b"),
                serviceOfferingId = UUID.fromString("891b5344-83c6-453a-8521-1cdfa17f920b"),
                networkInterfaces = Seq(
                  NetworkInterface(
                    id = UUID.fromString("674aeb81-c153-4b39-b462-85d47c304efc"),
                    networkId = UUID.fromString("95b8b9cf-fd93-4601-b037-915b2bf5d5a2"),
                    secondaryIps = Seq.empty
                  ),
                  NetworkInterface(
                    id = UUID.fromString("ace7cb25-6b7a-4315-b519-59d1fde5666f"),
                    networkId = UUID.fromString("a72d1fb8-c754-42f2-8bad-295084c4f981"),
                    secondaryIps = Seq.empty
                  )
                ),
                memory = 512 //scalastyle:ignore
              ))))
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
