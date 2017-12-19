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
package com.bwsw.cloudstack.entities.requests.tag

import java.util.UUID

import com.bwsw.cloudstack.entities.requests.tag.types.UserTagType
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.responses
import com.bwsw.cloudstack.entities.util.requests.TestConstants.ParameterValues.{DUMMY_KEY, DUMMY_VALUE}
import com.bwsw.cloudstack.entities.util.responses.{Tag, TagFindResponse}
import com.bwsw.cloudstack.entities.util.responses.user.UserCreateResponse
import com.bwsw.cloudstack.entities.TestEntities
import org.scalatest.{Outcome, fixture}

class TagCreateRequestIntegrationTestSuite extends fixture.FlatSpec with TestEntities {
  val tagNumber = 3

  case class FixtureParam(resourceType: UserTagType, resourceIds: Set[UUID])

  def withFixture(test: OneArgTest): Outcome = {
    val userId = UUID.randomUUID()
    val resourceType = new UserTagType()
    val resourceIds: Set[UUID] = Set(userId)

    val userCreationSettings = UserCreateRequest.Settings(
      accountName = "admin",
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $userId"
    )
    val userCreateRequest = new UserCreateRequest(userCreationSettings).withId(userId).request
    mapper.deserialize[UserCreateResponse](executor.executeRequest(userCreateRequest))

    val theFixture = FixtureParam(resourceType, resourceIds)

    withFixture(test.toNoArgTest(theFixture))
  }

  it should s"create $tagNumber tags using a request which contains only required parameters" in { fixture =>

    val expectedTags = (0 until tagNumber).map(x => responses.Tag(DUMMY_KEY + x, DUMMY_VALUE)).toList

    val settings = TagCreateRequest.Settings(
      resourceType = fixture.resourceType,
      resourceIds = fixture.resourceIds,
      tags = expectedTags
    )
    val request = new TagCreateRequest(settings).request
    executor.executeRequest(request)

    checkTagCreation(settings, expectedTags)
  }

  it should s"create $tagNumber tags using a request which contains only required parameters and a parameter with incorrect key" in { fixture =>
    val incorrectParameter = UUID.randomUUID().toString
    val expectedTags = (0 until tagNumber).map(x => responses.Tag(DUMMY_KEY + x, DUMMY_VALUE)).toList

    val settings = TagCreateRequest.Settings(
      resourceType = fixture.resourceType,
      resourceIds = fixture.resourceIds,
      tags = expectedTags
    )
    val request = new TagCreateRequest(settings).request.addParameter(incorrectParameter, DUMMY_VALUE)
    executor.executeRequest(request)

    checkTagCreation(settings, expectedTags)
  }

  private def checkTagCreation(settings: TagCreateRequest.Settings, expectedTags: List[responses.Tag]): Unit = {
    val tagFindRequest = new TagFindRequest().withResource(settings.resourceIds.head).request
    val tags = mapper.deserialize[TagFindResponse](executor.executeRequest(tagFindRequest)).tags.maybeTags

    assert(
      tags.isDefined &&
        tags.get.forall(tag => expectedTags.exists(expected => checkTagEquality(settings, expected, tag)))
    )
  }

  private def checkTagEquality(settings: TagCreateRequest.Settings,
                               expectedTag: responses.Tag,
                               retrievedTag: Tag): Boolean = {
    expectedTag.key == retrievedTag.key &&
      expectedTag.value == retrievedTag.value &&
      settings.resourceType.toString == retrievedTag.resourceType &&
      settings.resourceIds.contains(retrievedTag.resourceId)
  }
}
