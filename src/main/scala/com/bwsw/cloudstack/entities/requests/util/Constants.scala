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
package com.bwsw.cloudstack.entities.requests.util

object Constants {
  object Parameters {
    val ACCOUNT = "account"
    val ACCOUNT_ID = "accountid"
    val ACCOUNT_TYPE = "accounttype"
    val DOMAIN_ID = "domainid"
    val EMAIL = "email"
    val FIRST_NAME = "firstname"
    val ID = "id"
    val KEY = "key"
    val LAST_NAME = "lastname"
    val LIST_ALL = "listAll"
    val NAME = "name"
    val NETWORK_DOMAIN = "networkdomain"
    val PASSWORD = "password"
    val RESOURCE_TYPE = "resourcetype"
    val RESOURCE_ID = "resourceid"
    val RESOURCE_IDS = "resourceids"
    val RESPONSE = "response"
    val ROLE_ID = "roleid"
    val TIMEZONE = "timezone"
    val USER_ID = "userid"
    val USER_NAME = "username"
    val VALUE = "value"
  }

  object Commands {
    val CREATE_ACCOUNT = "createAccount"
    val CREATE_TAGS = "createTags"
    val LIST_ACCOUNTS = "listAccounts"
  }

}
