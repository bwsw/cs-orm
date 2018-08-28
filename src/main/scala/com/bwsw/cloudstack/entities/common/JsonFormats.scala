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

import com.bwsw.cloudstack.entities.common.JsonFormats._
import com.bwsw.cloudstack.entities.events.Constants.{Events, FieldNames}
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import spray.json.DefaultJsonProtocol._
import spray.json._

trait JsonFormats {

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

  implicit val unknownEventJsonReader: RootJsonReader[UnknownEvent] = (json: JsValue) => UnknownEvent(json)

  protected val basicEvents: TypedEventParser = {
    case (Events.ACCOUNT_CREATE, JsObject(fields))
      if fields.isDefinedAt(FieldNames.EntityUuid) =>
      implicitly[JsonReader[AccountCreateEvent]]

    case (Events.ACCOUNT_DELETE, JsObject(fields))
      if fields.isDefinedAt(FieldNames.EntityUuid) =>
      implicitly[JsonReader[AccountDeleteEvent]]

    case (Events.USER_CREATE, JsObject(fields))
      if fields.isDefinedAt(FieldNames.EntityUuid) =>
      implicitly[JsonReader[UserCreateEvent]]

    case (Events.VM_CREATE, JsObject(fields))
      if fields.isDefinedAt(FieldNames.EntityUuid) =>
      implicitly[JsonReader[VirtualMachineCreateEvent]]

    case (Events.VM_DESTROY, JsObject(fields))
      if fields.isDefinedAt(FieldNames.EntityUuid) =>
      implicitly[JsonReader[VirtualMachineDestroyEvent]]
  }


  /**
    * Returns parser for [[CloudStackEvent]]
    *
    * @param jsObjectParser partial function to parse [[JsObject]] to [[CloudStackEvent]]
    * @param otherParsers   partial functions to parse [[JsValue]] to [[CloudStackEvent]]
    * @return parser for [[CloudStackEvent]]
    */
  def jsonToCloudStackEvent(jsObjectParser: PartialFunction[JsValue, CloudStackEvent] = jsObjectToCloudStackEvent(),
                            otherParsers: Seq[PartialFunction[JsValue, CloudStackEvent]] = Seq.empty
                           ): RootJsonReader[CloudStackEvent] = {
    val parser = otherParsers.foldLeft(jsObjectParser)(_ orElse _)

    json: JsValue => parser.applyOrElse(json, unknownEventJsonReader.read)
  }

  /**
    * Returns partial function to parse [[JsObject]] to [[CloudStackEvent]]
    *
    * @param typedEvents   partial function to parse events with field `event`
    * @param untypedEvents partial function to parse events without field `event`
    * @return partial function to parse [[JsObject]] to [[CloudStackEvent]]
    */
  def jsObjectToCloudStackEvent(typedEvents: TypedEventParser = PartialFunction.empty,
                                untypedEvents: UntypedEventParser = PartialFunction.empty
                               ): PartialFunction[JsValue, CloudStackEvent] = {
    case jsObject: JsObject =>
      val reader =
        jsObject.fields.get(FieldNames.Event) match {
          case Some(JsString(eventType)) =>
            basicEvents
              .orElse(typedEvents)
              .lift((eventType, jsObject))
              .getOrElse(unknownEventJsonReader)

          case _ =>
            untypedEvents
              .lift(jsObject)
              .getOrElse(unknownEventJsonReader)
        }

      reader.read(jsObject)
  }
}


object JsonFormats {
  type TypedEventParser = PartialFunction[(String, JsObject), JsonReader[_ <: CloudStackEvent]]
  type UntypedEventParser = PartialFunction[JsObject, JsonReader[_ <: CloudStackEvent]]
}
