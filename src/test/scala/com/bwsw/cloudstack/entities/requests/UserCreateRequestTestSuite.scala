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

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
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
    new ApacheCloudStackApiCommandParameter("response","json"),
    new ApacheCloudStackApiCommandParameter("account", settings.accountName),
    new ApacheCloudStackApiCommandParameter("email", settings.email),
    new ApacheCloudStackApiCommandParameter("firstname", settings.firstName),
    new ApacheCloudStackApiCommandParameter("lastname", settings.lastName),
    new ApacheCloudStackApiCommandParameter("password", settings.password),
    new ApacheCloudStackApiCommandParameter("username", settings.username)
  )

  "Instance creation" should "create request with default parameters" in {
    val request = new UserCreateRequest(settings)

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }

  "withId" should "add id parameter into request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("userid", userId.toString))
    val request = new UserCreateRequest(settings)

    assert(request.withId(userId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withAccountName" should "add account name parameter into request" in {
    val timezone = "GMT+0700"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("timezone", timezone))
    val request = new UserCreateRequest(settings)

    assert(request.withTimeZone(TimeZone.getTimeZone(timezone)).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add account name parameter into request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("domainid", domainId))
    val request = new UserCreateRequest(settings)

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }
}
