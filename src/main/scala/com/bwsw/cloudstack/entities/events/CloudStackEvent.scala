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

import java.util.UUID

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo}
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.events.vm.{VirtualMachineCreateEvent, VirtualMachineDestroyEvent}
import com.bwsw.cloudstack.entities.events.Constants._

/**
  * A base structure of Apache CloudStack event.
  */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
  property = "event",
  defaultImpl = classOf[CloudStackEvent],
  visible = true,
  include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes(Array(
  new Type(value = classOf[UserCreateEvent], name = Events.USER_CREATE),
  new Type(value = classOf[AccountCreateEvent], name = Events.ACCOUNT_CREATE),
  new Type(value = classOf[AccountDeleteEvent], name = Events.ACCOUNT_DELETE),
  new Type(value = classOf[VirtualMachineCreateEvent], name = Events.VM_CREATE),
  new Type(value = classOf[VirtualMachineDestroyEvent], name = Events.VM_DESTROY)
))
class CloudStackEvent(val status: Option[String],
                      val entityuuid: Option[UUID],
                      @JsonProperty("event") val action: Option[String])
