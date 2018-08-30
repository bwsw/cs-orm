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

import com.bwsw.cloudstack.entities.common.JsonFormats._
import com.bwsw.cloudstack.entities.events.Constants.{Events, FieldNames}
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, UnknownEvent}
import spray.json._

trait JsonFormats {

  protected val basicEvents: TypedEventParser = {
    case (Events.ACCOUNT_CREATE, _) => implicitly[JsonReader[AccountCreateEvent]]
    case (Events.ACCOUNT_DELETE, _) => implicitly[JsonReader[AccountDeleteEvent]]
    case (Events.USER_CREATE, _) => implicitly[JsonReader[UserCreateEvent]]
    case (Events.VM_CREATE, _) => implicitly[JsonReader[VirtualMachineCreateEvent]]
    case (Events.VM_DESTROY, _) => implicitly[JsonReader[VirtualMachineDestroyEvent]]
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

    json: JsValue => parser.applyOrElse(json, implicitly[RootJsonReader[UnknownEvent]].read)
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
              .getOrElse(implicitly[RootJsonReader[UnknownEvent]])

          case _ =>
            untypedEvents
              .lift(jsObject)
              .getOrElse(implicitly[RootJsonReader[UnknownEvent]])
        }

      reader.read(jsObject)
  }
}


object JsonFormats {
  type TypedEventParser = PartialFunction[(String, JsObject), JsonReader[_ <: CloudStackEvent]]
  type UntypedEventParser = PartialFunction[JsObject, JsonReader[_ <: CloudStackEvent]]
}
