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

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.bwsw.cloudstack.entities.events.Constants.{Events, FieldNames}
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.{Failure, Success, Try}

trait JsonFormats {

  private val logger = LoggerFactory.getLogger(classOf[JsonFormats])
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][ ]HH:mm:ss[ ]X")

  implicit val uuidJsonFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID = {
      json match {
        case JsString(x) => UUID.fromString(x)
        case x => deserializationError("Expected UUID as JsString, but got " + x)
      }
    }

    override def write(obj: UUID): JsValue = JsString(obj.toString)
  }

  implicit val offsetDateTimeJsonFormat: JsonFormat[OffsetDateTime] = new JsonFormat[OffsetDateTime] {
    override def read(json: JsValue): OffsetDateTime = {
      json match {
        case JsString(x) => OffsetDateTime.parse(x, dateTimeFormatter)
        case x => deserializationError("Expected OffsetDateTime as JsString, but got " + x)
      }
    }

    override def write(obj: OffsetDateTime): JsValue =
      JsString(obj.format(dateTimeFormatter))
  }

  implicit val accountCreateEventJsonFormat: RootJsonFormat[AccountCreateEvent] =
    jsonFormat3(AccountCreateEvent)
  implicit val accountDeleteEventJsonFormat: RootJsonFormat[AccountDeleteEvent] =
    jsonFormat3(AccountDeleteEvent)
  implicit val userCreateEventJsonFormat: RootJsonFormat[UserCreateEvent] =
    jsonFormat3(UserCreateEvent)
  implicit val virtualMachineCreateEventJsonFormat: RootJsonFormat[VirtualMachineCreateEvent] =
    jsonFormat3(VirtualMachineCreateEvent)
  implicit val virtualMachineDestroyEventJsonFormat: RootJsonFormat[VirtualMachineDestroyEvent] =
    jsonFormat3(VirtualMachineDestroyEvent)

  implicit val cloudStackEventJsonFormat: RootJsonFormat[CloudStackEvent] = new RootJsonFormat[CloudStackEvent] {
    override def read(json: JsValue): CloudStackEvent = {
      Try {
        val fields = json.asJsObject.fields
        val eventField = fields.get(FieldNames.Event)
        val eventDateTimeField = fields.get(FieldNames.EventDateTime)

        (eventField, eventDateTimeField) match {
          case (Some(JsString(Events.ACCOUNT_CREATE)), Some(_)) => json.convertTo[AccountCreateEvent]
          case (Some(JsString(Events.ACCOUNT_DELETE)), Some(_)) => json.convertTo[AccountDeleteEvent]
          case (Some(JsString(Events.USER_CREATE)), Some(_)) => json.convertTo[UserCreateEvent]
          case (Some(JsString(Events.VM_CREATE)), Some(_)) => json.convertTo[VirtualMachineCreateEvent]
          case (Some(JsString(Events.VM_DESTROY)), Some(_)) => json.convertTo[VirtualMachineDestroyEvent]
          case _ => UnknownEvent(json)
        }
      } match {
        case Success(event) => event
        case Failure(exception) =>
          logger.warn(s"Cannot parse event: $json", exception)
          UnknownEvent(json)
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
