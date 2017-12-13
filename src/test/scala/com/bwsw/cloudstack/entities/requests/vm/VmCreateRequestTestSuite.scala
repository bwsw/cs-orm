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
package com.bwsw.cloudstack.entities.requests.vm

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.Constants.{ParameterValues, Commands}
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class VmCreateRequestTestSuite extends FlatSpec {

  val settings = VmCreateRequest.Settings(
    serviceOfferingId = UUID.randomUUID(),
    templateId = UUID.randomUUID(),
    zoneId = UUID.randomUUID()
  )

  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE, ParameterValues.JSON),
    new ApacheCloudStackApiCommandParameter(SERVICE_OFFERING_ID, settings.serviceOfferingId),
    new ApacheCloudStackApiCommandParameter(TEMPLATE_ID, settings.templateId),
    new ApacheCloudStackApiCommandParameter(ZONE_ID, settings.zoneId)
  )

  it should "create a request with predefined and specified (via constructor) parameters" in {
    val request = new VmCreateRequest(settings)

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
    assert(request.request.getCommand == Commands.DEPLOY_VIRTUAL_MACHINE)
  }

  "withDomain" should "add a domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new VmCreateRequest(settings)

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withAccountName" should "add an account name parameter to a request" in {
    val accountName = "name"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT, accountName))
    val request = new VmCreateRequest(settings)

    assert(request.withAccountName(accountName).request.getParameters.asScala.toSet == expectedParameters)
  }
}
