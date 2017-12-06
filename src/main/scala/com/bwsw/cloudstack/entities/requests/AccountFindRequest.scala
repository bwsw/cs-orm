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
package com.bwsw.cloudstack.entities.requests

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.util.Constants.Comands
import com.bwsw.cloudstack.entities.requests.traits.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for retrieving account list
  */
class AccountFindRequest extends Request {
  override val request = new ApacheCloudStackRequest(Comands.LIST_ACCOUNTS)
    .addParameter(RESPONSE,"json")
    .addParameter(LIST_ALL, true)

  /**
    * Add account id parameter to a request.
    */
  def withId(id: UUID): AccountFindRequest = {
    request.addParameter(ID, id)
    this
  }

  /**
    * Add account name parameter to a request.
    */
  def withName(name: String): AccountFindRequest = {
    request.addParameter(NAME, name)
    this
  }

  /**
    * Add domain id parameter to a request.
    */
  def withDomain(id: UUID): AccountFindRequest = {
    request.addParameter(DOMAIN_ID, id)
    this
  }
}
