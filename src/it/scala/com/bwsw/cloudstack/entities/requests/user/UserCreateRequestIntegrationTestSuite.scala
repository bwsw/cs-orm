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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.PasswordAuthenticationClientCreator
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest
import com.bwsw.cloudstack.entities.requests.domain.DomainCreateRequest
import com.bwsw.cloudstack.entities.responses.domain.DomainCreateResponse
import com.bwsw.cloudstack.entities.responses.user._
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import com.bwsw.cloudstack.entities.{Executor, TestEntities}
import org.scalatest.FlatSpec

class UserCreateRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "create a user using a request which contains only required parameters" in {
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString

    val settings = UserCreateRequest.Settings(
      accountName = adminAccount,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val request = new UserCreateRequest(settings).getRequest

    checkUserCreation(request, settings)
  }

  it should "create a user using a request which contains the required and optional parameters" in {
    import AccountCreateRequest.Settings

    val accountId = UUID.randomUUID()
    val userId = UUID.randomUUID()
    val accountName = UUID.randomUUID().toString
    val domainName = UUID.randomUUID().toString
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString
    val timeZone = TimeZone.getTimeZone("GMT+7:00")

    val domainCreateRequest = new DomainCreateRequest(domainName).getRequest
    val newDomainId = mapper.deserialize[DomainCreateResponse](executor.executeRequest(domainCreateRequest)).domainEntity.domain.id

    val accountCreateRequest = new AccountCreateRequest(Settings(
      _type = AccountCreateRequest.User,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "password",
      username = "username"
    ))
    accountCreateRequest.withName(accountName)
    accountCreateRequest.withDomain(newDomainId)
    accountCreateRequest.withId(accountId)

    executor.executeRequest(accountCreateRequest.getRequest)

    val settings = UserCreateRequest.Settings(
      accountName = accountName,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val userCreateRequest = new UserCreateRequest(settings)
    userCreateRequest.withId(userId)
    userCreateRequest.withDomain(newDomainId)
    userCreateRequest.withTimeZone(timeZone)

    val expectedUser = User(
      id = userId,
      accountId,
      account = accountName,
      email = settings.email,
      firstname = settings.firstName,
      lastname = settings.lastName,
      username = userName,
      domainId = newDomainId,
      timezone = Some(timeZone.getID)
    )

    val actualUser = mapper.deserialize[UserCreateResponse](executor.executeRequest(userCreateRequest.getRequest)).userEntity.user

    assert(actualUser == expectedUser)

    val testRequest = new UserFindRequest().getRequest

    assert(checkPasswordCorrectness(settings.username, settings.password, s"/$domainName", testRequest))

  }

  it should "create a user using a request which contains only required parameters and a parameter with incorrect key" in {
    val userName = UUID.randomUUID().toString
    val password = UUID.randomUUID().toString
    val incorrectParameter = UUID.randomUUID().toString

    val settings = UserCreateRequest.Settings(
      accountName = adminAccount,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = password,
      username = userName
    )
    val request = new UserCreateRequest(settings).getRequest.addParameter(incorrectParameter, ParameterValues.DUMMY_VALUE)

    checkUserCreation(request, settings)
  }

  private def checkUserCreation(request: ApacheCloudStackRequest, settings: UserCreateRequest.Settings): Unit = {

    val actualUser = mapper.deserialize[UserCreateResponse](executor.executeRequest(request)).userEntity.user

    assert(
      actualUser.account == settings.accountName &&
        actualUser.email == settings.email &&
        actualUser.firstname == settings.firstName &&
        actualUser.lastname == settings.lastName &&
        actualUser.username == settings.username
    )

    val testRequest = new UserFindRequest().getRequest

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
