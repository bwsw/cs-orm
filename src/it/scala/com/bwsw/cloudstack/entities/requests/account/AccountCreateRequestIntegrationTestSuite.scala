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
import com.bwsw.cloudstack.entities.requests.domain.DomainCreateRequest
import com.bwsw.cloudstack.entities.responses.account.{Account, AccountCreateResponse}
import com.bwsw.cloudstack.entities.responses.domain.DomainCreateResponse
import com.bwsw.cloudstack.entities.responses.user
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import com.bwsw.cloudstack.entities.{Executor, TestEntities}
import org.scalatest.FlatSpec

class AccountCreateRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "create an account using a request which contains only required parameters" in {
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString

    val settings = AccountCreateRequest.Settings(
      _type = AccountCreateRequest.User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val request = new AccountCreateRequest(settings).getRequest

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

    val domainCreateRequest = new DomainCreateRequest(domainName).getRequest
    val newDomainId = mapper.deserialize[DomainCreateResponse](executor.executeRequest(domainCreateRequest)).domainEntity.domain.id

    val accountCreateSettings = Settings(
      _type = AccountCreateRequest.User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = UUID.randomUUID().toString,
      username = UUID.randomUUID().toString
    )

    val accountCreateRequest = new AccountCreateRequest(accountCreateSettings)
    accountCreateRequest.withId(accountId)
    accountCreateRequest.withName(accountName)
    accountCreateRequest.withDomain(newDomainId)
    accountCreateRequest.withRole(accountRole._1)
    accountCreateRequest.withNetworkDomain(networkDomain)
    accountCreateRequest.withTimeZone(timeZone)
    accountCreateRequest.withUserId(userId)

    val actualAccount = mapper.deserialize[AccountCreateResponse](executor.executeRequest(accountCreateRequest.getRequest)).accountEntity.account

    val expectedAccount = Account(
      id = accountId,
      name = accountName,
      accountType = accountCreateSettings._type.numericValue,
      domainId = newDomainId,
      networkDomain = networkDomain,
      users = List(user.User(id = userId,
        accountId = accountId,
        account = accountName,
        email = accountCreateSettings.email,
        firstname = accountCreateSettings.firstName,
        lastname = accountCreateSettings.lastName,
        username = accountCreateSettings.username,
        domainId = newDomainId,
        timezone = Some(timeZone.getID)
      )),
      roleType = accountRole._2
    )

    assert(actualAccount == expectedAccount)

    val testRequest = new AccountFindRequest().getRequest

    assert(checkPasswordCorrectness(accountCreateSettings.username, accountCreateSettings.password, s"/$domainName", testRequest))

  }

  it should "create an account using a request which contains only required parameters and a parameter with incorrect key" in {
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString
    val incorrectParameter = UUID.randomUUID().toString

    val settings = AccountCreateRequest.Settings(
      _type = AccountCreateRequest.User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val request = new AccountCreateRequest(settings).getRequest.addParameter(incorrectParameter, ParameterValues.DUMMY_VALUE)

    checkAccountCreation(request, settings)
  }

  private def checkAccountCreation(request: ApacheCloudStackRequest, settings: AccountCreateRequest.Settings): Unit = {

    val testAccount = mapper.deserialize[AccountCreateResponse](executor.executeRequest(request)).accountEntity.account

    assert(
      testAccount.accountType == settings._type.numericValue &&
        testAccount.users.head.email == settings.email &&
        testAccount.users.head.firstname == settings.firstName &&
        testAccount.users.head.lastname == settings.lastName &&
        testAccount.users.head.username == settings.username
    )

    val testRequest = new AccountFindRequest().getRequest

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
