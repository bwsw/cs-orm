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
package com.bwsw.cloudstack.entities.requests.user

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.Constants.Commands
import com.bwsw.cloudstack.entities.requests.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for creating user
  *
  * @param settings required parameters for user creation, more info see UserCreateRequest.Settings
  */
class UserCreateRequest(settings: UserCreateRequest.Settings) extends Request {
  override protected[entities] val request = new ApacheCloudStackRequest(Commands.CREATE_USER)
    .addParameter(RESPONSE, "json")
    .addParameter(ACCOUNT, settings.accountName)
    .addParameter(EMAIL, settings.email)
    .addParameter(FIRST_NAME, settings.firstName)
    .addParameter(LAST_NAME, settings.lastName)
    .addParameter(PASSWORD, settings.password)
    .addParameter(USER_NAME, settings.username)

  /**
    * Add domain id parameter to a request.
    * Has to be accompanied with the account parameter.
    */
  def withDomain(id: UUID): UserCreateRequest = {
    request.addParameter(DOMAIN_ID, id)
    this
  }

  /**
    * Add time zone parameter to a request
    */
  def withTimeZone(timeZone: TimeZone): UserCreateRequest ={
    request.addParameter(TIMEZONE, timeZone.toZoneId)
    this
  }

  /**
    * Add user id parameter to a request.
    * Required for adding account from external provisioning system
    */
  def withId(id: UUID): UserCreateRequest = {
    request.addParameter(USER_ID, id)
    this
  }
}

object UserCreateRequest {
  /**
    * Class is responsible to provide user creation settings
    *
    * @param accountName user account name.
    *                    If it isn't specified, a username will be used as an account name.
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
