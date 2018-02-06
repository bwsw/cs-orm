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

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.responses.vm.{VirtualMachineFindResponse, VirtualMachineList}
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import com.bwsw.cloudstack.entities.util.requests.RequestExecutionHandler
import org.scalatest.FlatSpec

class VmFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  it should "retrieve json string if request contains only default parameters" in {
    val vmFindRequest = new VmFindRequest
    val response = mapper.deserialize[VirtualMachineFindResponse](executor.executeRequest(vmFindRequest.getRequest))

    assert(response.entityList.isInstanceOf[VirtualMachineList])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of id parameter does not exist" in {
    val vmId = UUID.randomUUID()
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withId(vmId)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of account name parameter does not exist" in {
    val accountName = UUID.randomUUID().toString
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withAccountName(accountName)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of domain parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withDomain(domainId)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of group parameter does not exist" in {
    val groupId = UUID.randomUUID()
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withGroup(groupId)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of user parameter does not exist" in {
    val userId = UUID.randomUUID()
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withUser(userId)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of zone parameter does not exist" in {
    val zoneId = UUID.randomUUID()
    val vmFindRequest = new VmFindRequest()
    vmFindRequest.withZone(zoneId)

    assert(RequestExecutionHandler.entityNotExist(vmFindRequest))
  }

  it should "ignore a parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new VmFindRequest().getRequest.addParameter(incorrectParameterKey, ParameterValues.DUMMY_VALUE)
    val response = mapper.deserialize[VirtualMachineFindResponse](executor.executeRequest(request))

    assert(response.entityList.isInstanceOf[VirtualMachineList])
  }
}
