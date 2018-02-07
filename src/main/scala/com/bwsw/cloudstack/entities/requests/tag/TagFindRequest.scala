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
package com.bwsw.cloudstack.entities.requests.tag

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.tag.types.TagType
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.Request

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for retrieving tag list
  */
class TagFindRequest extends Request {
  override protected val request = new ApacheCloudStackRequest(Commands.LIST_TAGS)
    .addParameter(RESPONSE, ParameterValues.JSON)
    .addParameter(LIST_ALL, true)

  /**
    * Add an account name parameter to a request.
    * The client domain will be used if another domain is not specified
    */
  def withAccountName(name: String): Unit = {
    addParameter(ACCOUNT, name)
  }

  /**
    * Add a domain id parameter to a request.
    */
  def withDomain(id: UUID): Unit = {
    addParameter(DOMAIN_ID, id)
  }

  /**
    * Add a tag key parameter to a request.
    */
  def withKey(key: String): Unit = {
    addParameter(KEY, key)
  }

  /**
    * Add a resource id parameter to a request.
    */
  def withResource(id: UUID): Unit = {
    addParameter(RESOURCE_ID, id)
  }

  /**
    * Add a resource type parameter to a request.
    */
  def withResourceType(resourceType: TagType): Unit = {
    addParameter(RESOURCE_TYPE, resourceType.name)
  }

  /**
    * Add a tag value parameter to a request.
    */
  def withValue(value: String): Unit = {
    addParameter(VALUE, value)
  }
}
