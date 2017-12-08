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
package com.bwsw.cloudstack.entities.requests

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.util.Constants.Commands
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.traits.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for creating a virtual machine
  *
  * @param settings required parameters for vm creation, more info see VmCreateRequest.Settings
  */
class VmCreateRequest(settings: VmCreateRequest.Settings) extends Request {

  override val request: ApacheCloudStackRequest = new ApacheCloudStackRequest(Commands.DEPLOY_VIRTUAL_MACHINE)
    .addParameter(RESPONSE, "json")
    .addParameter(SERVICE_OFFERING_ID, settings.serviceOfferingId)
    .addParameter(TEMPLATE_ID, settings.templateId)
    .addParameter(ZONE_ID, settings.zoneId)

  /**
    * Add an account name parameter to a request.
    */
  def withAccountName(name: String): VmCreateRequest = {
    request.addParameter(ACCOUNT, name)
    this
  }

  /**
    * Add a domain id parameter to a request.
    */
  def withDomain(id: UUID): VmCreateRequest = {
    request.addParameter(DOMAIN_ID, id)
    this
  }
}

object VmCreateRequest {
  /**
    * Class is responsible for providing vm creation settings.
    *
    * @param serviceOfferingId the UUID of the service offering for the virtual machine
    * @param templateId the UUID of the template for the virtual machine
    * @param zoneId the UUID of availability zone for the virtual machine
    */
  case class Settings(serviceOfferingId: UUID, templateId: UUID, zoneId: UUID)
}
