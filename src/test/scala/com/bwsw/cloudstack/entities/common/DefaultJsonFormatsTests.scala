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
import com.bwsw.cloudstack.entities.events.jobresults.{NetworkInterface, VirtualMachineJobResult}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, JobResult, UnknownEvent}
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


  it should "deserialize VirtualMachineCreateEvent properly" in {
    val jsonString =
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
      """.stripMargin

    val expectedEvent = VirtualMachineCreateEvent(
      status = Some(Statuses.SUCCEEDED),
      eventDateTime = None, //scalastyle:ignore
      jobResult = Some(
        JobResult(
          path = "org.apache.cloudstack.api.response.UserVmResponse/virtualmachine/",
          result = Right(
            VirtualMachineJobResult(
              id = UUID.fromString("991558d9-5dfe-4272-85aa-98cbf7c10830"),
              account = "aaa",
              domainId = UUID.fromString("bcaf64de-56ff-44ef-9f4a-d5959cb3d63b"),
              serviceOfferingId = UUID.fromString("891b5344-83c6-453a-8521-1cdfa17f920b"),
              networkInterfaces = Seq(
                NetworkInterface(
                  id = UUID.fromString("674aeb81-c153-4b39-b462-85d47c304efc"),
                  secondaryIps = Seq.empty
                ),
                NetworkInterface(
                  id = UUID.fromString("ace7cb25-6b7a-4315-b519-59d1fde5666f"),
                  secondaryIps = Seq.empty
                )
              ),
              memory = 512 //scalastyle:ignore
            )
          )
        )
      )
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize VirtualMachineDestroyEvent with field 'event' properly" in {
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
      description = Some("destroying vm: 14"),
      cmdInfo = None
    )

    jsonString.parseJson.convertTo[CloudStackEvent] shouldBe expectedEvent
  }


  it should "deserialize VirtualMachineDestroyEvent with field 'commandEventType' properly" in {
    val jsonString =
      """{
        |  "jobId": "794aa39e-fb85-464f-9683-312bcf3da894",
        |  "commandEventType": "VM.DESTROY",
        |  "processStatus": "0",
        |  "cmdInfo": "{\"response\":\"json\",\"expunge\":\"true\",\"uuid\":\"aaf1457c-5f5e-4a34-a5bb-13677b71b932\"}",
        |  "instanceType": "VirtualMachine",
        |  "jobResult": "org.apache.cloudstack.api.response.UserVmResponse/null/{\"securitygroup\":[],\"nic\":[],\"affinitygroup\":[],\"tags\":[]}",
        |  "resultCode": "0",
        |  "instanceUuid": "aaf1457c-5f5e-4a34-a5bb-13677b71b932",
        |  "user": "88c34c6b-f503-11e7-a402-0242ac110003",
        |  "command": "org.apache.cloudstack.api.command.admin.vm.DestroyVMCmdByAdmin",
        |  "account": "88c31833-f503-11e7-a402-0242ac110003",
        |  "status": "SUCCEEDED"
        |}
      """.stripMargin

    val expectedEvent = VirtualMachineDestroyEvent(
      status = Some(Statuses.SUCCEEDED),
      entityuuid = None,
      eventDateTime = None,
      description = None,
      cmdInfo = Some(CmdInfo(Map(
        "response" -> "json",
        "expunge" -> "true",
        "uuid" -> "aaf1457c-5f5e-4a34-a5bb-13677b71b932"
      )))
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
