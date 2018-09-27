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

import com.bwsw.cloudstack.KeyAuthenticationClientCreator
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.common.JsonMapper
import com.bwsw.cloudstack.entities.responses.account.Account
import com.bwsw.cloudstack.entities.responses.user.User
import com.bwsw.cloudstack.entities.responses.vm.VirtualMachine

trait TestData {
  private val retryDelay = 1000
  val executorSettings = Executor.Settings(Array("localhost:9000"), retryDelay)
  val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("apiKey", "secretKey"))
  val jsonMapper = new JsonMapper(true)

  object Response {
    def getTagResponseJson(key: String, value: String): String =
      "{\"listtagsresponse\":{\"count\":1,\"tag\":[{\"key\":\"" + s"$key" + "\",\"value\":\"" + s"$value" + "\"}]}}"

    def getAccountResponseJson(account: Account): String =
      "{\"listaccountsresponse\":{\"count\":1,\"account\":" + jsonMapper.serialize(List(account)) + "}}"

    def getUserResponseJson(user: User): String = {
      "{\"listusersresponse\":{\"count\":1,\"user\":" + jsonMapper.serialize(List(user)) + "}}"
    }

    def getVmResponseJson(vm: VirtualMachine): String =
      "{\"listvirtualmachinesresponse\":{\"virtualmachine\":" + jsonMapper.serialize(List(vm)) + "}}"

    def getResponseWithEmptyVmList: String = "{\"listvirtualmachinesresponse\":{}}"

    def getResponseWithEmptyAccountList: String = "{\"listaccountsresponse\":{}}"

    def getResponseWithEmptyUserList: String = "{\"listusersresponse\":{}}"

    def getResponseWithEmptyTagList: String = "{\"listtagsresponse\":{}}"
  }

  private val accountId = UUID.randomUUID()
  private val accountName = "name"
  private val domainId = UUID.randomUUID()

  val testUser = User(
    id = UUID.randomUUID(),
    accountId = accountId,
    account = accountName,
    email = "user@example.com",
    firstname = "first",
    lastname = "last",
    username = "user",
    domainId = domainId,
    None
  )

  val testAccount = Account(
    id = accountId,
    name = accountName,
    accountType = 1,
    domainId = domainId,
    networkDomain = UUID.randomUUID().toString,
    List(testUser),
    roleType = "Admin"
  )

  val testVm = VirtualMachine(
    id = UUID.randomUUID(),
    zoneId = UUID.randomUUID(),
    templateId = UUID.randomUUID(),
    serviceOfferingId = UUID.randomUUID(),
    accountName = accountName,
    domainId = domainId,
    networkInterfaces = Seq.empty
  )
}
