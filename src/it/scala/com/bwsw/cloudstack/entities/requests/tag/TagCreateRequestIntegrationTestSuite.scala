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

import com.bwsw.cloudstack.entities.requests.tag.types.{TagType, UserTagType}
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues.{DUMMY_KEY, DUMMY_VALUE}
import com.bwsw.cloudstack.entities.util.responses.{TagTestFindResponse, TestTag}
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.responses.tag.Tag
import com.bwsw.cloudstack.entities.responses.user.UserCreateResponse
import org.scalatest.{Outcome, fixture}

class TagCreateRequestIntegrationTestSuite extends fixture.FlatSpec with TestEntities {
  val tagNumber = 3

  case class FixtureParam(resourceIds: Set[UUID], resourceType: TagType = UserTagType)

  def withFixture(test: OneArgTest): Outcome = {
    val userId = UUID.randomUUID()
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

    val theFixture = FixtureParam(resourceIds)

    withFixture(test.toNoArgTest(theFixture))
  }

  it should s"create $tagNumber tags using a request which contains only required parameters" in { fixture =>

    val expectedTags = (0 until tagNumber).map(x => Tag(DUMMY_KEY + x, DUMMY_VALUE)).toList

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
    val expectedTags = (0 until tagNumber).map(x => Tag(DUMMY_KEY + x, DUMMY_VALUE)).toList

    val settings = TagCreateRequest.Settings(
      resourceType = fixture.resourceType,
      resourceIds = fixture.resourceIds,
      tags = expectedTags
    )
    val request = new TagCreateRequest(settings).request.addParameter(incorrectParameter, DUMMY_VALUE)
    executor.executeRequest(request)

    checkTagCreation(settings, expectedTags)
  }

  private def checkTagCreation(settings: TagCreateRequest.Settings, expectedTags: List[Tag]): Unit = {
    val tagFindRequest = new TagFindRequest().withResource(settings.resourceIds.head).request
    val tags = mapper.deserialize[TagTestFindResponse](executor.executeRequest(tagFindRequest)).tags.maybeTags

    assert(
      tags.isDefined &&
        tags.get.forall(tag => expectedTags.exists(expected => checkTagEquality(settings, expected, tag)))
    )
  }

  private def checkTagEquality(settings: TagCreateRequest.Settings,
                               expectedTag: Tag,
                               retrievedTag: TestTag): Boolean = {
    expectedTag.key == retrievedTag.key &&
      expectedTag.value == retrievedTag.value &&
      settings.resourceType.name == retrievedTag.resourceType &&
      settings.resourceIds.contains(retrievedTag.resourceId)
  }
}
