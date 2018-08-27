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

import com.bwsw.cloudstack.entities.events.Constants.Events
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.{Failure, Success, Try}

trait JsonFormats {

  implicit val uuidJsonFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID =
      UUID.fromString(StringJsonFormat.read(json))

    override def write(obj: UUID): JsValue =
      StringJsonFormat.write(obj.toString)
  }
  implicit val accountCreateEventJsonFormat: RootJsonFormat[AccountCreateEvent] =
    jsonFormat2(AccountCreateEvent)
  implicit val accountDeleteEventJsonFormat: RootJsonFormat[AccountDeleteEvent] =
    jsonFormat2(AccountDeleteEvent)
  implicit val userCreateEventJsonFormat: RootJsonFormat[UserCreateEvent] =
    jsonFormat2(UserCreateEvent)
  implicit val virtualMachineCreateEventJsonFormat: RootJsonFormat[VirtualMachineCreateEvent] =
    jsonFormat2(VirtualMachineCreateEvent)
  implicit val virtualMachineDestroyEventJsonFormat: RootJsonFormat[VirtualMachineDestroyEvent] =
    jsonFormat2(VirtualMachineDestroyEvent)

  implicit val cloudStackEventJsonFormat: RootJsonFormat[CloudStackEvent] = new RootJsonFormat[CloudStackEvent] {
    override def read(json: JsValue): CloudStackEvent = {
      Try {
        json.asJsObject.fields("event") match {
          case JsString(Events.ACCOUNT_CREATE) => json.convertTo[AccountCreateEvent]
          case JsString(Events.ACCOUNT_DELETE) => json.convertTo[AccountDeleteEvent]
          case JsString(Events.USER_CREATE) => json.convertTo[UserCreateEvent]
          case JsString(Events.VM_CREATE) => json.convertTo[VirtualMachineCreateEvent]
          case JsString(Events.VM_DESTROY) => json.convertTo[VirtualMachineDestroyEvent]
          case _ => UnknownEvent(json)
        }
      } match {
        case Success(event) => event
        case Failure(_) => UnknownEvent(json)
      }
    }

    override def write(obj: CloudStackEvent): JsValue = {
      obj match {
        case event: AccountCreateEvent => event.toJson
        case event: AccountDeleteEvent => event.toJson
        case event: UserCreateEvent => event.toJson
        case event: VirtualMachineCreateEvent => event.toJson
        case event: VirtualMachineDestroyEvent => event.toJson
        case UnknownEvent(json) => json
      }
    }
  }
}


object JsonFormats extends JsonFormats
