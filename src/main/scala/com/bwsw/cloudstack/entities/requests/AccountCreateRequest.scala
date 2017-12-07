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
import com.bwsw.cloudstack.entities.requests.util.Constants.Commands
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.traits.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for creating an account with a new user
  *
  * @param settings required parameters for account creation, more info see AccountCreateRequest.Settings
  *                 NOTE: account type and role ID are not included in settings, but at least one of them must be defined using
  *                       withType or withRole methods.
  */
class AccountCreateRequest(settings: AccountCreateRequest.Settings) extends Request {

  override val request = new ApacheCloudStackRequest(Commands.CREATE_ACCOUNT)
    .addParameter(RESPONSE,"json")
    .addParameter(EMAIL, settings.email)
    .addParameter(FIRST_NAME, settings.firstName)
    .addParameter(LAST_NAME, settings.lastName)
    .addParameter(PASSWORD, settings.password)
    .addParameter(USER_NAME, settings.username)

  /**
    * Add a domain id parameter to a request.
    */
  def withDomain(id: UUID): AccountCreateRequest = {
    request.addParameter(DOMAIN_ID, id)
    this
  }

  /**
    * Add a time zone parameter to a request.
    */
  def withTimeZone(timeZone: TimeZone): AccountCreateRequest ={
    request.addParameter(TIMEZONE, timeZone.toZoneId)
    this
  }

  /**
    * Add an account id parameter to a request,
    * required for adding account from external provisioning system.
    */
  def withId(id: UUID): AccountCreateRequest = {
    request.addParameter(ACCOUNT_ID, id)
    this
  }

  /**
    * Add an account name parameter to a request.
    * If it isn't specified, a username will be used as an account name.
    */
  def withName(name: String): AccountCreateRequest = {
    request.addParameter(ACCOUNT, name)
    this
  }

  /**
    * Add an account type parameter to a request.
    * Specify 0 for user, 1 for root admin, and 2 for domain admin.
    */
  def withType(accountType: Int): AccountCreateRequest = {
    request.addParameter(ACCOUNT_TYPE, accountType)
    this
  }

  /**
    * Add a network domain parameter for the account's networks to a request.
    */
  def withNetworkDomain(domain: String): AccountCreateRequest = {
    request.addParameter(NETWORK_DOMAIN, domain)
    this
  }

  /**
    * Add an account role parameter to a request.
    * Specify 1 for admin, 2 for resource admin, 3 for domain admin, 4 for user.
    */
  def withRole(role: Int): AccountCreateRequest = {
    request.addParameter(ROLE_ID, role)
    this
  }

  /**
    * Add an user id parameter to a request.
    * required for adding account from external provisioning system
    */
  def withUserId(id: UUID): AccountCreateRequest = {
    request.addParameter(USER_ID, id)
    this
  }
}

object AccountCreateRequest {
  /**
    * Class is responsible for providing account creation settings.
    *
    * @param email user email
    * @param firstName user first name
    * @param lastName user last name
    * @param password user text password (Default hashed to SHA256SALT).
    * @param username user unique username
    */
  case class Settings(email: String,
                      firstName: String,
                      lastName: String,
                      password: String,
                      username: String)
}
