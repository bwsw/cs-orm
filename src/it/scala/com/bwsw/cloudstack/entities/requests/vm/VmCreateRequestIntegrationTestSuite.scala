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
import com.bwsw.cloudstack.entities.requests.Constants
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys
import com.bwsw.cloudstack.entities.requests.account.AccountFindRequest
import com.bwsw.cloudstack.entities.responses.account.AccountFindResponse
import com.bwsw.cloudstack.entities.responses.vm.{VirtualMachine, VirtualMachineCreateResponse, VirtualMachineFindResponse}
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import org.scalatest.{FlatSpec, Matchers}

class VmCreateRequestIntegrationTestSuite
  extends FlatSpec
    with TestEntities
    with Matchers {

  val serviceOfferingId: UUID = retrievedServiceOffering.id
  val memory: Long = retrievedServiceOffering.memory
  val templateId: UUID = retrievedTemplateId
  val zoneId: UUID = retrievedZoneId

  it should "create a vm using a request which contains only required parameters" in {
    val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))

    checkVmCreation(vmCreateRequest.getRequest)
  }

  it should "create a vm using a request which contains the required and optional parameters" in {
    val domainId = retrievedAdminDomainId
    val accountFindRequest = new AccountFindRequest()
    accountFindRequest.withDomain(domainId)

    val accountName = mapper.deserialize[AccountFindResponse](
      executor.executeRequest(accountFindRequest.getRequest)
    ).entityList.entities.get.head.name

    val vmCreateRequest = new VmCreateRequest(VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId))
    vmCreateRequest.withDomainAccount(accountName, domainId)

    val response = executor.executeRequest(vmCreateRequest.getRequest)

    val vmId = mapper.deserialize[VirtualMachineCreateResponse](response).vm.id

    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withId(vmId)

    val actualVm = mapper.deserialize[VirtualMachineFindResponse](executor.executeRequest(vmFindRequest.getRequest))
      .entityList.entities.get.head
    val expectedVm = VirtualMachine(
      vmId,
      zoneId,
      templateId,
      serviceOfferingId,
      accountName,
      domainId,
      Seq.empty,
      memory
    )

    actualVm.copy(networkInterfaces = Seq.empty) shouldBe expectedVm
    actualVm.networkInterfaces.size should be > 0

    val networkInterface = actualVm.networkInterfaces.head

    val addIpRequest = new ApacheCloudStackRequest("addIpToNic")
      .addParameter("nicid", networkInterface.id)
      .addParameter(ParameterKeys.RESPONSE, Constants.ParameterValues.JSON)

    executor.executeRequest(addIpRequest)

    val actualVmWithSecondaryIps = mapper.deserialize[VirtualMachineFindResponse](executor.executeRequest(vmFindRequest.getRequest))
      .entityList.entities.get.head

    actualVmWithSecondaryIps.networkInterfaces.map(_.id) should contain(networkInterface.id)
    val networkInterfaceWithSecondaryIps = actualVmWithSecondaryIps.networkInterfaces.find(_.id == networkInterface.id).get
    networkInterfaceWithSecondaryIps.secondaryIps.length should be >= 1
    networkInterfaceWithSecondaryIps.secondaryIps.foreach { secondaryIp =>
      Some(secondaryIp.id) shouldBe defined
    }
  }

  it should "create a vm using a request which contains only required parameters and a parameter with incorrect key" in {
    val incorrectParameter = UUID.randomUUID().toString
    val request = new VmCreateRequest(
      VmCreateRequest.Settings(serviceOfferingId, templateId, zoneId)
    )

    request.addParameter(incorrectParameter, ParameterValues.DUMMY_VALUE)

    checkVmCreation(request.getRequest)
  }

  private def checkVmCreation(request: ApacheCloudStackRequest): Unit = {
    val response = executor.executeRequest(request)

    val vmId = mapper.deserialize[VirtualMachineCreateResponse](response).vm.id

    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withId(vmId)

    val actualVm = mapper.deserialize[VirtualMachineFindResponse](executor.executeRequest(vmFindRequest.getRequest))
      .entityList.entities.get.head

    assert(actualVm.id == vmId &&
      actualVm.serviceOfferingId == serviceOfferingId &&
      actualVm.templateId == templateId &&
      actualVm.zoneId == zoneId)
  }

}
