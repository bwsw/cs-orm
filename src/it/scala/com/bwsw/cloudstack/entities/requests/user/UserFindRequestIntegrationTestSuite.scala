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
package com.bwsw.cloudstack.entities.requests.user

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Request
import com.bwsw.cloudstack.entities.responses.UserResponse
import com.bwsw.cloudstack.entities.util.requests.TestConstants.ParameterValues
import org.scalatest.FlatSpec

import scala.util.{Failure, Success, Try}

class UserFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  it should "retrieve json string if request contains only default parameters" in {
    val userFindRequest = new UserFindRequest
    val response = mapper.deserialize[UserResponse](executor.executeRequest(userFindRequest.getRequest))

    assert(response.isInstanceOf[UserResponse])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of id parameter does not exist" in {
    val id = UUID.randomUUID()
    val userFindRequest = new UserFindRequest().withId(id)

    assert(tryExecuteRequest(userFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of domain parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val userFindRequest = new UserFindRequest().withDomain(domainId)

    assert(tryExecuteRequest(userFindRequest))
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of account name parameter does not exist" in {
    val accountName = UUID.randomUUID().toString
    val userFindRequest = new UserFindRequest().withAccountName(accountName)

    assert(tryExecuteRequest(userFindRequest))
  }

  it should "return an empty list of users if entity with a specified value of name parameter does not exist" in {
    val name = UUID.randomUUID().toString
    val userFindRequest = new UserFindRequest().withName(name)
    val response = mapper.deserialize[UserResponse](executor.executeRequest(userFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "retrieve json string if request contains default parameters and parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new UserFindRequest().getRequest.addParameter(incorrectParameterKey, ParameterValues.DUMMY_VALUE)
    val response = mapper.deserialize[UserResponse](executor.executeRequest(request))

    assert(response.isInstanceOf[UserResponse])
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
