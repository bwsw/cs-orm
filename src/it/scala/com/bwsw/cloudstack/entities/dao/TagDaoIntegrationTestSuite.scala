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
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.RootAdmin
import com.bwsw.cloudstack.entities.requests.tag.types.{AccountTagType, TagType, UserTagType, VmTagType}
import com.bwsw.cloudstack.entities.requests.tag.{TagCreateRequest, TagFindRequest}
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmFindRequest}
import com.bwsw.cloudstack.entities.responses.Tag
import org.scalatest.FlatSpec

class TagDaoIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "retrieve user tags after their creation" in {
    val userId = UUID.randomUUID()
    createUser(userId)
    checkTagCreation(userId, UserTagType)
  }

  it should "retrieve account tags after their creation" in {
    val accountId = UUID.randomUUID()
    createAccount(accountId, UUID.randomUUID().toString, None)
    checkTagCreation(accountId, AccountTagType)
  }

  it should "retrieve virtual machine tags after their creation" in {
    val vmId = createVm
    checkTagCreation(vmId, VmTagType)
  }

  private def createUser(id: UUID): Unit = {
    val userDao = new UserDao(executor, mapper)

    val userCreationSettings = UserCreateRequest.Settings(
      accountName="admin",
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $id"
    )
    val userCreateRequest = new UserCreateRequest(userCreationSettings).withId(id)
    userDao.create(userCreateRequest)
  }

  private def createAccount(id: UUID, name: String, domainId: Option[UUID]): Unit = {
    val accountDao = new AccountDao(executor, mapper)

    val accountCreationSettings = AccountCreateRequest.Settings(
      _type = RootAdmin,
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $id"
    )
    val accountCreateRequest = new AccountCreateRequest(accountCreationSettings).withId(id).withName(name)

    domainId match {
      case Some(x) => accountCreateRequest.withDomain(x)
      case _ =>
    }

    accountDao.create(accountCreateRequest)
  }

  private def createVm: UUID = {
    val accountId = UUID.randomUUID()

    val vmDao = new VirtualMachineDao(executor, mapper)

    val firstAccountName = UUID.randomUUID().toString

    val domainId = retrievedAdminDomainId

    createAccount(accountId, firstAccountName, Some(domainId))

    val serviceOfferingId = retrievedServiceOfferingId
    val templateId = retrievedTemplateId
    val zoneId = retrievedZoneId

    val vmCreationSettings = VmCreateRequest.Settings(
      serviceOfferingId,
      templateId,
      zoneId
    )
    val vmCreateRequest = new VmCreateRequest(vmCreationSettings).withDomainAccount(firstAccountName, domainId)
    vmDao.create(vmCreateRequest)

    val findByAccountNameRequest = new VmFindRequest().withAccountName(firstAccountName)
    vmDao.find(findByAccountNameRequest).head.id
  }

  private def checkTagCreation(resourceId: UUID, tagType: TagType) = {
    val firstTag = Tag("key1", "value1")
    val secondTag = Tag("key2", "value2")
    val thirdTag = Tag("key3", "value3")

    val tagDao = new TagDao(executor, mapper)
    val findRequest = new TagFindRequest().withResource(resourceId).withResourceType(tagType)
    assert(tagDao.find(findRequest).isEmpty)

    val firstCreateTagRequest = new TagCreateRequest(TagCreateRequest.Settings(
      tagType,
      Set(resourceId),
      List(firstTag)
    ))

    tagDao.create(firstCreateTagRequest)

    assert(tagDao.find(findRequest) == Set(firstTag))

    val secondCreateTagRequest = new TagCreateRequest(TagCreateRequest.Settings(
      tagType,
      Set(resourceId),
      List(secondTag, thirdTag)
    ))

    tagDao.create(secondCreateTagRequest)

    assert(tagDao.find(findRequest) == Set(firstTag, secondTag, thirdTag))
  }
}
