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
package com.bwsw.cloudstack.entities.requests.vm

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for retrieving vm list
  */
class VmFindRequest extends Request {
  override protected val request = new ApacheCloudStackRequest(Commands.LIST_VMS)
    .addParameter(RESPONSE, ParameterValues.JSON)
    .addParameter(LIST_ALL, true)

  /**
    * Add a vm id parameter to a request.
    */
  def withId(id: UUID): VmFindRequest = {
    request.addParameter(ID, id)
    this
  }

  /**
    * Add an account name parameter to a request.
    * The client domain will be used if another domain is not specified
    */
  def withAccountName(name: String): VmFindRequest = {
    request.addParameter(ACCOUNT, name)
    this
  }

  /**
    * Add a domain id parameter to a request.
    */
  def withDomain(id: UUID): VmFindRequest = {
    request.addParameter(DOMAIN_ID, id)
    this
  }

  /**
    * Add a group id parameter to a request.
    */
  def withGroup(id: UUID): VmFindRequest = {
    request.addParameter(GROUP_ID, id)
    this
  }

  /**
    * Add a user id parameter to a request.
    */
  def withUser(id: UUID): VmFindRequest = {
    request.addParameter(USER_ID, id)
    this
  }

  /**
    * Add a zone id parameter to a request.
    */
  def withZone(id: UUID): VmFindRequest = {
    request.addParameter(ZONE_ID, id)
    this
  }
}
