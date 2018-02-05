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

object Constants {
  object ParameterKeys {
    val ACCOUNT = "account"
    val ACCOUNT_ID = "accountid"
    val ACCOUNT_TYPE = "accounttype"
    val AVAILABLE = "available"
    val DOMAIN_ID = "domainid"
    val EMAIL = "email"
    val FIRST_NAME = "firstname"
    val GROUP_ID = "groupid"
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
    val SERVICE_OFFERING_ID = "serviceofferingid"
    val TEMPLATE_FILTER = "templatefilter"
    val TEMPLATE_ID = "templateid"
    val TIMEZONE = "timezone"
    val USER_ID = "userid"
    val USER_NAME = "username"
    val VALUE = "value"
    val ZONE_ID = "zoneid"
  }

  object ParameterValues {
    val FEATURED = "featured"
    val JSON = "json"
  }

  object Commands {
    val CREATE_ACCOUNT = "createAccount"
    val CREATE_DOMAIN = "createDomain"
    val CREATE_TAGS = "createTags"
    val CREATE_USER = "createUser"
    val LIST_ACCOUNTS = "listAccounts"
    val LIST_DOMAINS = "listDomains"
    val LIST_SERVICE_OFFERINGS = "listServiceOfferings"
    val LIST_TAGS = "listTags"
    val LIST_TEMPLATES = "listTemplates"
    val LIST_USERS = "listUsers"
    val LIST_VMS = "listVirtualMachines"
    val LIST_ZONES = "listZones"
    val DEPLOY_VIRTUAL_MACHINE = "deployVirtualMachine"
    val DELETE_ACCOUNT = "deleteAccount"
    val DELETE_VM = "destroyVirtualMachine"
  }

}
