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

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.PasswordAuthenticationClientCreator
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.User
import com.bwsw.cloudstack.entities.util.requests.DomainCreateRequest
import com.bwsw.cloudstack.entities.util.requests.TestConstants.ParameterValues
import com.bwsw.cloudstack.entities.util.responses.account._
import com.bwsw.cloudstack.entities.util.responses.domain.DomainCreateResponse
import com.bwsw.cloudstack.entities.util.responses.user.TestUser
import com.bwsw.cloudstack.entities.{Executor, TestEntities}
import org.scalatest.FlatSpec

class AccountCreateRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "create an account using a request which contains only required parameters" in {
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString

    val settings = AccountCreateRequest.Settings(
      _type = User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val request = new AccountCreateRequest(settings).request

    checkAccountCreation(request, settings)
  }

  it should "create an account using a request which contains the required and optional parameters" in {
    import AccountCreateRequest.Settings

    val accountId = UUID.randomUUID()
    val accountName = UUID.randomUUID().toString
    val domainName = UUID.randomUUID().toString
    val userId = UUID.randomUUID()
    val networkDomain = UUID.randomUUID().toString
    val timeZone = TimeZone.getTimeZone("GMT+7:00")
    val accountRole = (4, "User")

    val domainCreateRequest = new DomainCreateRequest(domainName).request
    val newDomainId = mapper.deserialize[DomainCreateResponse](executor.executeRequest(domainCreateRequest)).domainEntity.domainId.id

    val accountCreateSettings = Settings(
      _type = User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = UUID.randomUUID().toString,
      username = UUID.randomUUID().toString
    )

    val accountCreateRequest = new AccountCreateRequest(accountCreateSettings)
      .withId(accountId)
      .withName(accountName)
      .withDomain(newDomainId)
      .withRole(accountRole._1)
      .withNetworkDomain(networkDomain)
      .withTimeZone(timeZone)
      .withUserId(userId)

    val actualAccount = mapper.deserialize[AccountCreateResponse](executor.executeRequest(accountCreateRequest.request)).accountEntity.account

    val expectedAccount = TestAccount(
      id = accountId,
      accountType = accountCreateSettings._type.numericValue,
      user = Set(TestUser(id = userId,
        email = accountCreateSettings.email,
        firstname = accountCreateSettings.firstName,
        lastname = accountCreateSettings.lastName,
        username = accountCreateSettings.username,
        domainId = newDomainId,
        timezone = Some(timeZone.getID),
        account = accountName)),
      domainId = newDomainId,
      networkDomain = networkDomain,
      roleType = accountRole._2
    )

    assert(actualAccount == expectedAccount)

    val testRequest = new AccountFindRequest().request

    assert(checkPasswordCorrectness(accountCreateSettings.username, accountCreateSettings.password, s"/$domainName", testRequest))

  }

    it should "create an account using a request which contains only required parameters and a parameter with incorrect key" in {
      val userName = UUID.randomUUID().toString
      val password = UUID.randomUUID().toString
      val incorrectParameter = UUID.randomUUID().toString

      val settings = AccountCreateRequest.Settings(
        _type = User,
        email = "e@e",
        firstName = "fn",
        lastName = "ln",
        password = password,
        username = userName
      )
      val request = new AccountCreateRequest(settings).request.addParameter(incorrectParameter, ParameterValues.DUMMY_VALUE)

      checkAccountCreation(request, settings)
    }

  private def checkAccountCreation(request: ApacheCloudStackRequest, settings: AccountCreateRequest.Settings): Unit = {

    val testAccount = mapper.deserialize[AccountCreateResponse](executor.executeRequest(request)).accountEntity.account

    assert(
      testAccount.accountType == settings._type.numericValue &&
        testAccount.user.head.email == settings.email &&
        testAccount.user.head.firstname == settings.firstName &&
        testAccount.user.head.lastname == settings.lastName &&
        testAccount.user.head.username == settings.username
    )

    val testRequest = new AccountFindRequest().request

    assert(checkPasswordCorrectness(settings.username, settings.password, "/", testRequest))
  }

  private def checkPasswordCorrectness(username: String, password: String, domain: String, request: ApacheCloudStackRequest): Boolean = {
    import PasswordAuthenticationClientCreator.Settings
    val executor = new Executor(
      executorSettings,
      new PasswordAuthenticationClientCreator(Settings(username, password, domain)),
      true
    )

    executor.executeRequest(request).isInstanceOf[String]
  }
}
