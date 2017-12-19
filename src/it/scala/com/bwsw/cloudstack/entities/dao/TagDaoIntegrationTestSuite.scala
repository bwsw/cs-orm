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
package com.bwsw.cloudstack.entities.dao

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.tag.types.UserTagType
import com.bwsw.cloudstack.entities.requests.tag.{TagCreateRequest, TagFindRequest}
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.responses.Tag
import org.scalatest.FlatSpec

class TagDaoIntegrationTestSuite extends FlatSpec with TestEntities {
  val userDao = new UserDao(executor, mapper)
  val userId = UUID.randomUUID()

  val userCreationSettings = UserCreateRequest.Settings(
    accountName="admin",
    email = "e@e",
    firstName = "first",
    lastName = "last",
    password = "passwd",
    username = s"username $userId"
  )
  val userCreateRequest = new UserCreateRequest(userCreationSettings).withId(userId)
  userDao.create(userCreateRequest)

  it should "retrieve tags after their creation" in {
    val firstTag = Tag("key1", "value1")
    val secondTag = Tag("key2", "value2")
    val thirdTag = Tag("key3", "value3")

    val tagDao = new TagDao(executor, mapper)
    val findRequest = new TagFindRequest().withResource(userId)
    assert(tagDao.find(findRequest).isEmpty)

    val firstCreateTagRequest = new TagCreateRequest(TagCreateRequest.Settings(
      UserTagType,
      Set(userId),
      List(firstTag)
    ))

    tagDao.create(firstCreateTagRequest)

    assert(tagDao.find(findRequest) == Set(firstTag))

    val secondCreateTagRequest = new TagCreateRequest(TagCreateRequest.Settings(
      UserTagType,
      Set(userId),
      List(secondTag, thirdTag)
    ))

    tagDao.create(secondCreateTagRequest)

    assert(tagDao.find(findRequest) == Set(firstTag, secondTag, thirdTag))
  }
}
