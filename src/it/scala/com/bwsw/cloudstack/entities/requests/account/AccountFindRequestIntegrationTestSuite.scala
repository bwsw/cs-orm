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
package com.bwsw.cloudstack.entities.requests.account

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.responses.account.{AccountFindResponse, AccountList}
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import com.bwsw.cloudstack.entities.util.requests.RequestExecutionHandler
import org.scalatest.FlatSpec

class AccountFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  it should "retrieve json string if request contains only default parameters" in {
    val accountFindRequest = new AccountFindRequest
    val response = mapper.deserialize[AccountFindResponse](executor.executeRequest(accountFindRequest.getRequest))

    assert(response.entityList.isInstanceOf[AccountList])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of id parameter does not exist" in {
    val accountId = UUID.randomUUID()
    val accountFindRequest = new AccountFindRequest()
    accountFindRequest.withId(accountId)

    assert(RequestExecutionHandler.entityNotExist(accountFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of domain parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val accountFindRequest = new AccountFindRequest()
    accountFindRequest.withDomain(domainId)

    assert(RequestExecutionHandler.entityNotExist(accountFindRequest))
  }

  it should "return an empty list of accounts if entity with a specified value of name parameter does not exist" in {
    val name = UUID.randomUUID().toString
    val accountFindRequest = new AccountFindRequest()
    accountFindRequest.withName(name)

    val response = mapper.deserialize[AccountFindResponse](executor.executeRequest(accountFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "ignore a parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new AccountFindRequest().getRequest.addParameter(incorrectParameterKey, ParameterValues.DUMMY_VALUE)
    val response = mapper.deserialize[AccountFindResponse](executor.executeRequest(request))

    assert(response.entityList.isInstanceOf[AccountList])
  }
}
