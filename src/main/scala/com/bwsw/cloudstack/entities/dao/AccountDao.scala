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

import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.common.JsonMapper
import com.bwsw.cloudstack.entities.requests.account.{AccountCreateRequest, AccountFindRequest}
import com.bwsw.cloudstack.entities.responses.{Account, AccountResponse}

class AccountDao(executor: Executor, mapper: JsonMapper) extends GenericDao[Account, String](executor, mapper) {
  override protected type F = AccountFindRequest
  override protected type C = AccountCreateRequest

  override def create(request: C): Unit = super.create(request)

  override def find(request: F): List[Account] = {
    val response = executor.executeRequest(request.request)
    mapper.deserialize[AccountResponse](response).entityList.entities.getOrElse(List.empty[Account])
  }
}
