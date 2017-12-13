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
package com.bwsw.cloudstack.entities

import java.util.UUID

import com.bwsw.cloudstack.entities.dao.AccountDao
import com.bwsw.cloudstack.entities.requests.account.{AccountCreateRequest, AccountFindRequest}
import org.scalatest.FlatSpec

class AccountDaoIntegrationTestSuite extends FlatSpec with TestEntities {
  val accountDao = new AccountDao(executor, mapper)
  val firstAccountId = UUID.randomUUID()
  val secondAccountId = UUID.randomUUID()

  it should "retrieve accounts after their creation" in {
    val firstFindRequest = new AccountFindRequest().withId(firstAccountId)
    val initAccounts = accountDao.find(firstFindRequest)

    assert(initAccounts.isEmpty)

    val firstAccountCreationSettings = AccountCreateRequest.Settings(
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $firstAccountId"
    )

    val firstAccountCreateRequest = new AccountCreateRequest(firstAccountCreationSettings).withId(firstAccountId).withType(1)
    accountDao.create(firstAccountCreateRequest)

    val secondFindRequest = new AccountFindRequest().withId(firstAccountId)
    val updatedAccounts = accountDao.find(secondFindRequest)
    assert(updatedAccounts.size == 1 && updatedAccounts.head.id == firstAccountId)

    val secondAccountCreationSettings = AccountCreateRequest.Settings(
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $secondAccountId"
    )

    val secondAccountCreateRequest = new AccountCreateRequest(secondAccountCreationSettings).withId(secondAccountId).withRole(1)

    accountDao.create(secondAccountCreateRequest)

    val allUserIds = accountDao.find(new AccountFindRequest).map(_.id)

    assert(allUserIds.contains(firstAccountId) && allUserIds.contains(secondAccountId))
  }
}
