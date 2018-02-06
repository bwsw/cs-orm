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

import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Request
import com.bwsw.cloudstack.entities.responses.AccountResponse
import com.bwsw.cloudstack.entities.util.requests.TestConstants.ParameterValues
import org.scalatest.FlatSpec

import scala.util.{Failure, Success, Try}

class AccountFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  it should "retrieve json string if request contains only default parameters" in {
    val accountFindRequest = new AccountFindRequest
    val response = mapper.deserialize[AccountResponse](executor.executeRequest(accountFindRequest.getRequest))

    assert(response.isInstanceOf[AccountResponse])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of id parameter does not exist" in {
    val accountId = UUID.randomUUID()
    val accountFindRequest = new AccountFindRequest().withId(accountId)

    assert(tryExecuteRequest(accountFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of domain parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val accountFindRequest = new AccountFindRequest().withDomain(domainId)

    assert(tryExecuteRequest(accountFindRequest))
  }

  it should "return an empty list of accounts if entity with a specified value of name parameter does not exist" in {
    val name = UUID.randomUUID().toString
    val accountFindRequest = new AccountFindRequest().withName(name)
    val response = mapper.deserialize[AccountResponse](executor.executeRequest(accountFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "retrieve json string if request contains default parameters and parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new AccountFindRequest().getRequest.addParameter(incorrectParameterKey, ParameterValues.DUMMY_VALUE)
    val response = mapper.deserialize[AccountResponse](executor.executeRequest(request))

    assert(response.isInstanceOf[AccountResponse])
  }

  private def tryExecuteRequest(request: Request): Boolean = {
    Try {
      executor.executeRequest(request.getRequest)
    } match {
      case Success(_) => false
      case Failure(e: ApacheCloudStackClientRequestRuntimeException) =>
        e.getStatusCode == 431
      case Failure(_: Throwable) => false
    }
  }

}
