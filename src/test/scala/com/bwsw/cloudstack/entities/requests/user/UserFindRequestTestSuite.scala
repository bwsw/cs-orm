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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class UserFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE, ParameterValues.JSON),
    new ApacheCloudStackApiCommandParameter(LIST_ALL, true)
  )

  it should "create a request with predefined parameters" in {
    val request = new UserFindRequest

    assert(request.getRequest.getParameters.asScala.toSet == defaultParameters)
    assert(request.getRequest.getCommand == Commands.LIST_USERS)
  }

  "withId" should "add id parameter to a request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ID, userId.toString))
    val request = new UserFindRequest
    request.withId(userId)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withAccountName" should "add account name parameter to a request" in {
    val accountName = "test"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT, accountName))
    val request = new UserFindRequest
    request.withAccountName(accountName)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new UserFindRequest
    request.withDomain(domainId)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withName" should "add user name parameter to a request" in {
    val userName = "userNameTest"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(USER_NAME, userName))
    val request = new UserFindRequest
    request.withName(userName)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  it should "create child UserFindRequest with one of parent parameters and one new parameter" in {
    val userName = "userNameTest"
    val testParameterValue = "testValue"
    val testParameterName = "testName"

    val expectedParameters = defaultParameters ++ Set(
      new ApacheCloudStackApiCommandParameter(USER_NAME, userName),
      new ApacheCloudStackApiCommandParameter(testParameterName, testParameterValue)
    )

    class TestUserFindRequest extends UserFindRequest {
      def withTestParameter(value: String): Unit = {
        addParameter(testParameterName, value)
      }
    }

    val request = new TestUserFindRequest
    request.withName(userName)
    request.withTestParameter(testParameterValue)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }
}
