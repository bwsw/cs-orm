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

import com.bwsw.cloudstack.entities.dao.UserDao
import com.bwsw.cloudstack.entities.requests.user.{UserCreateRequest, UserFindRequest}
import org.scalatest.FlatSpec

class UserDaoIntegrationTestSuite extends FlatSpec with TestEntities {
  val userDao = new UserDao(executor, mapper)
  val firstUserId = UUID.randomUUID()
  val secondUserId = UUID.randomUUID()

  it should "retrieve users after their creation" in {
    val firstFindRequest = new UserFindRequest().withId(firstUserId)
    val initUsers = userDao.find(firstFindRequest)

    assert(initUsers.isEmpty)

    val firstUserCreationSettings = UserCreateRequest.Settings(
      accountName="admin",
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $firstUserId"
    )
    val firstUserCreateRequest = new UserCreateRequest(firstUserCreationSettings).withId(firstUserId)
    userDao.create(firstUserCreateRequest)

    val secondFindRequest = new UserFindRequest().withId(firstUserId)
    val updatedUsers = userDao.find(secondFindRequest)
    assert(updatedUsers.size == 1 && updatedUsers.head.id == firstUserId)

    val secondUserCreationSettings = UserCreateRequest.Settings(
      accountName="admin",
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $secondUserId"
    )

    val secondUserCreateRequest = new UserCreateRequest(secondUserCreationSettings).withId(secondUserId)

    userDao.create(secondUserCreateRequest)

    val allUserIds = userDao.find(new UserFindRequest).map(_.id)

    assert(allUserIds.contains(firstUserId) && allUserIds.contains(secondUserId))
  }
}
