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
package com.bwsw.cloudstack.entities.responses.vm

import com.bwsw.cloudstack.entities.responses.common.{EntityFindResponse, EntityList}
import com.fasterxml.jackson.annotation.JsonProperty

case class VirtualMachineFindResponse(@JsonProperty("listvirtualmachinesresponse") override val entityList: VirtualMachineList)
  extends EntityFindResponse(entityList)

case class VirtualMachineList(@JsonProperty("virtualmachine") override val entities: Option[List[VirtualMachine]])
  extends EntityList(entities)