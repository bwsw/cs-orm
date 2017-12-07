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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.util.VmTagType
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class TagFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE,"json"),
    new ApacheCloudStackApiCommandParameter(LIST_ALL,"true")
  )

  it should "create a request with predefined parameters" in {
    val request = new TagFindRequest

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }

  "withAccountName" should "add an account name parameter to a request" in {
    val accountName = "name"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT, accountName))
    val request = new TagFindRequest

    assert(request.withAccountName(accountName).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add a domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new TagFindRequest

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withKey" should "add a key parameter to a request" in {
    val key = "key"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(KEY, key))
    val request = new TagFindRequest

    assert(request.withKey(key).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withResource" should "add a resource id parameter to a request" in {
    val resource = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(RESOURCE_ID, resource))
    val request = new TagFindRequest

    assert(request.withResource(resource).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withResourceType" should "add a resource type to a request" in {
    val resourceType = new VmTagType
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(RESOURCE_TYPE, resourceType.toString))
    val request = new TagFindRequest

    assert(request.withResourceType(resourceType).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withValue" should "add a tag value to a request" in {
    val value = "value"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(VALUE, value))
    val request = new TagFindRequest

    assert(request.withValue(value).request.getParameters.asScala.toSet == expectedParameters)
  }
}
