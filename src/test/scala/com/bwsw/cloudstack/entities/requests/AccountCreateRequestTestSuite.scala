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

class AccountCreateRequestTestSuite extends FlatSpec {
  val settings = AccountCreateRequest.Settings(
    email = "test@example.com",
    firstName = "fn",
    lastName = "ln",
    password = "password",
    username = "test"
  )

  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter("response","json"),
    new ApacheCloudStackApiCommandParameter("email", settings.email),
    new ApacheCloudStackApiCommandParameter("firstname", settings.firstName),
    new ApacheCloudStackApiCommandParameter("lastname", settings.lastName),
    new ApacheCloudStackApiCommandParameter("password", settings.password),
    new ApacheCloudStackApiCommandParameter("username", settings.username)
  )

  it should "create a request with predefined and specified (via constructor) parameters" in {
    val request = new AccountCreateRequest(settings)

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }

  "withId" should "add id parameter to a request" in {
    val accountId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("accountid", accountId.toString))
    val request = new AccountCreateRequest(settings)

    assert(request.withId(accountId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withTimeZone" should "add time zone parameter to a request" in {
    val timezone = "GMT+0700"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("timezone", timezone))
    val request = new AccountCreateRequest(settings)

    assert(request.withTimeZone(TimeZone.getTimeZone(timezone)).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add account name parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("domainid", domainId))
    val request = new AccountCreateRequest(settings)

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withName" should "add account name parameter to a request" in {
    val accountName = "name"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("account", accountName))
    val request = new AccountCreateRequest(settings)

    assert(request.withName(accountName).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withType" should "add account type parameter to a request" in {
    val accountType = 1
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("accounttype", accountType))
    val request = new AccountCreateRequest(settings)

    assert(request.withType(accountType).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withNetworkDomain" should "add network domain parameter to a request" in {
    val networkdomain = "root"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("networkdomain", networkdomain))
    val request = new AccountCreateRequest(settings)

    assert(request.withNetworkDomain(networkdomain).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withRole" should "add role id parameter to a request" in {
    val role = 1
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("roleid", role))
    val request = new AccountCreateRequest(settings)

    assert(request.withRole(role).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withUserId" should "add user id parameter to a request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("userid", userId))
    val request = new AccountCreateRequest(settings)

    assert(request.withUserId(userId).request.getParameters.asScala.toSet == expectedParameters)
  }
}
