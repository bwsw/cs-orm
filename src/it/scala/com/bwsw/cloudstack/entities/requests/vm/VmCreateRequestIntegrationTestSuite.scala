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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.account.AccountFindRequest
import com.bwsw.cloudstack.entities.util.responses.vm.{VmCreateResponse, VmTest, VmTestFindResponse}
import com.bwsw.cloudstack.entities.util.responses.account.AccountTestFindResponse
import org.scalatest.FlatSpec

class VmCreateRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  val serviceOfferingId = retievedServiceOfferingId
  val templateId = retrievedTemplateId
  val zoneId = retrievedZoneId

  it should "create vm use request which contains only required parameters" in {
    val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))

    checkVmCreation(vmCreateRequest.request)
  }

  it should "create vm use request which contains all available parameters" in {
    val domainId = retrievedDomainId
    val accountFindRequest = new AccountFindRequest().withDomain(domainId)
    val accountName = mapper.deserialize[AccountTestFindResponse](
      executor.executeRequest(accountFindRequest.request)
    ).entityList.entities.get.head.name

    val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))
      .withDomainAccount(accountName, domainId)
    val response = executor.executeRequest(vmCreateRequest.request)

    val vmId = mapper.deserialize[VmCreateResponse](response).vmId.id

    val vmFindRequest = new VmFindRequest().withId(vmId)

    val actualVm = mapper.deserialize[VmTestFindResponse](executor.executeRequest(vmFindRequest.request))
      .entityList.entities.get.head
    val expectedVm = VmTest(vmId, zoneId, templateId, serviceOfferingId, accountName, domainId)

    assert(actualVm == expectedVm)
  }

  it should "create vm use request which contains only required parameters and parameter with incorrect key" in {
    val incorrectParameter = UUID.randomUUID().toString
    val request = new VmCreateRequest(
      VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId)
    ).request.addParameter(incorrectParameter, "value")

    checkVmCreation(request)
  }

  def checkVmCreation(request: ApacheCloudStackRequest): Unit = {
    val response = executor.executeRequest(request)

    val vmId = mapper.deserialize[VmCreateResponse](response).vmId.id

    val vmFindRequest = new VmFindRequest().withId(vmId)

    val actualVm = mapper.deserialize[VmTestFindResponse](executor.executeRequest(vmFindRequest.request))
      .entityList.entities.get.head

    assert(actualVm.id == vmId &&
      actualVm.serviceofferingid == serviceOfferingId &&
      actualVm.templateid == templateId &&
      actualVm.zoneid == zoneId)
  }

}
