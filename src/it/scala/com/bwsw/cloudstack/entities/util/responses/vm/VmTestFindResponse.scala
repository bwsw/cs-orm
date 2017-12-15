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
package com.bwsw.cloudstack.entities.util.responses.vm

import java.util.UUID

import com.bwsw.cloudstack.entities.responses.{Entity, EntityList, EntityResponse}
import com.fasterxml.jackson.annotation.JsonProperty

case class VmTestFindResponse(@JsonProperty("listvirtualmachinesresponse") override val entityList: VmTestList) extends EntityResponse(entityList)

case class VmTestList(@JsonProperty("virtualmachine") override val entities: Option[List[VmTest]]) extends EntityList(entities)

case class VmTest(id: UUID,
                  zoneid: UUID,
                  templateid: UUID,
                  serviceofferingid: UUID,
                  @JsonProperty("account") accountName: String,
                  @JsonProperty("domainid") domain: UUID) extends Entity