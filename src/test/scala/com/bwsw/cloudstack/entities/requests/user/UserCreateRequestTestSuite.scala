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

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class UserCreateRequestTestSuite extends FlatSpec {
  val settings = UserCreateRequest.Settings(
    accountName = "accountName",
    email = "test@example.com",
    firstName = "fn",
    lastName = "ln",
    password = "password",
    username = "test"
  )

  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE, ParameterValues.JSON),
    new ApacheCloudStackApiCommandParameter(ACCOUNT, settings.accountName),
    new ApacheCloudStackApiCommandParameter(EMAIL, settings.email),
    new ApacheCloudStackApiCommandParameter(FIRST_NAME, settings.firstName),
    new ApacheCloudStackApiCommandParameter(LAST_NAME, settings.lastName),
    new ApacheCloudStackApiCommandParameter(PASSWORD, settings.password),
    new ApacheCloudStackApiCommandParameter(USER_NAME, settings.username)
  )

  it should "create a request with predefined and specified (via constructor) parameters" in {
    val request = new UserCreateRequest(settings)

    assert(request.getRequest.getParameters.asScala.toSet == defaultParameters)
    assert(request.getRequest.getCommand == Commands.CREATE_USER)
  }

  "withId" should "add id parameter to a request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(USER_ID, userId.toString))
    val request = new UserCreateRequest(settings)

    assert(request.withId(userId).getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withTimeZone" should "add time zone parameter to a request" in {
    val timezone = "GMT+0700"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(TIMEZONE, timezone))
    val request = new UserCreateRequest(settings)

    assert(request.withTimeZone(TimeZone.getTimeZone(timezone)).getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new UserCreateRequest(settings)

    assert(request.withDomain(domainId).getRequest.getParameters.asScala.toSet == expectedParameters)
  }
}
