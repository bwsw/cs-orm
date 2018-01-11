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

import com.bwsw.cloudstack.KeyAuthenticationClientCreator
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.common.JsonMapper

trait TestData {
  private val retryDelay = 1000
  val executorSettings = Executor.Settings(Array("localhost:9000"), retryDelay)
  val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("apiKey", "secretKey"))
  val jsonMapper = new JsonMapper(true)

  object Response {
    def getTagResponseJson(key: String, value: String): String =
      "{\"listtagsresponse\":{\"count\":1,\"tag\":[{\"key\":\"" + s"$key" + "\",\"value\":\"" + s"$value" + "\"}]}}"

    def getAccountResponseJson(account: String, user: String): String =
      "{\"listaccountsresponse\":{\"count\":1,\"account\":[{\"id\":\"" + s"$account" + "\"," +
        "\"user\":[{\"id\":\"" + s"$user" + "\",\"accountid\":\"" + s"$account" + "\"}]}]}}"

    def getUserResponseJson(user: String, account: String): String =
      "{\"listusersresponse\":{\"count\":1,\"user\":[{\"id\":\"" + s"$user" + "\", " +
        "\"accountid\":\" " + s"$account" + "\"}]}}"

    def getVmResponseJson(vm: String, accountName: String, domain: String): String =
      "{\"listvirtualmachinesresponse\":{\"virtualmachine\":[{\"id\":\"" + s"$vm" + "\"," +
        "\"account\":\"" + s"$accountName" + "\",\"domainid\":\"" + s"$domain" + "\"}]}}"

    def getResponseWithEmptyVmList: String = "{\"listvirtualmachinesresponse\":{}}"

    def getResponseWithEmptyAccountList: String = "{\"listaccountsresponse\":{}}"

    def getResponseWithEmptyUserList: String = "{\"listusersresponse\":{}}"

    def getResponseWithEmptyTagList: String = "{\"listtagsresponse\":{}}"
  }

}
