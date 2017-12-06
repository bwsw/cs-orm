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

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.traits.Request

/**
  * Class is responsible to create ApacheCloudStackRequest with specified parameters for creating user
  *
  * @param settings mandatory parameters for user creation, more info see UserCreateRequest.Settings
  */
class UserCreateRequest(settings: UserCreateRequest.Settings) extends Request {
  override val request = new ApacheCloudStackRequest("createUser")
    .addParameter("response","json")//toZoneId
    .addParameter("account", settings.accountName)
    .addParameter("email", settings.email)
    .addParameter("firstname", settings.firstName)
    .addParameter("lastname", settings.lastName)
    .addParameter("password", settings.password)
    .addParameter("username", settings.username)

  /**
    * Add domain id parameter into request.
    * Has to be accompanied with the account parameter.
    */
  def withDomain(id: UUID): UserCreateRequest = {
    request.addParameter("domainid", id)
    this
  }

  /**
    * Add time zone parameter into request
    */
  def withTimeZone(timeZone: TimeZone): UserCreateRequest ={
    request.addParameter("timezone", timeZone.toZoneId)
    this
  }

  /**
    * Add user id parameter into request.
    * Required for adding account from external provisioning system
    */
  def withId(id: UUID): UserCreateRequest = {
    request.addParameter("userid", id)
    this
  }
}

object UserCreateRequest {
  /**
    * Class is responsible to provide user creation settings
    *
    * @param accountName provides to create the user under the specified account.
    *                    If no account is specified, the username will be used as the account name.
    * @param email user email
    * @param firstName user first name
    * @param lastName user last name
    * @param password clear text password (Default hashed to SHA256SALT).
    * @param username unique username
    */
  case class Settings(accountName: String,
                      email: String,
                      firstName: String,
                      lastName: String,
                      password: String,
                      username: String)
}
