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
package com.bwsw.cloudstack.entities.events.jobresults

import java.util.UUID

import com.bwsw.cloudstack.entities.common.CommonJsonFormats.uuidJsonFormat
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class VirtualMachineJobResult(id: UUID,
                                         account: String,
                                         domainId: UUID,
                                         serviceOfferingId: UUID,
                                         networkInterfaces: Seq[NetworkInterface],
                                         memory: Long)


object VirtualMachineJobResult {

  implicit val virtualMachineCreateJobResultJsonFormat: RootJsonFormat[VirtualMachineJobResult] =
    jsonFormat(
      VirtualMachineJobResult.apply,
      "id",
      "account",
      "domainid",
      "serviceofferingid",
      "nic",
      "memory"
    )
}
