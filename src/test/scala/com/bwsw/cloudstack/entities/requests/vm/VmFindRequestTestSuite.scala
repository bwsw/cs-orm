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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.account.AccountFindRequest
import com.bwsw.cloudstack.entities.requests.Constants.Parameters._
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class VmFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE,"json"),
    new ApacheCloudStackApiCommandParameter(LIST_ALL,"true")
  )

  it should "create a request with predefined parameters" in {
    val request = new AccountFindRequest

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }

  "withId" should "add an id parameter to a request" in {
    val vmId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ID, vmId))
    val request = new VmFindRequest

    assert(request.withId(vmId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withAccountName" should "add an account name parameter to a request" in {
    val accountName = "test"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT, accountName))
    val request = new VmFindRequest

    assert(request.withAccountName(accountName).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add a domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new VmFindRequest

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withGroup" should "add a group id parameter to a request" in {
    val groupId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(GROUP_ID, groupId))
    val request = new VmFindRequest

    assert(request.withGroup(groupId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withUser" should "add a user id parameter to a request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(USER_ID, userId))
    val request = new VmFindRequest

    assert(request.withUser(userId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withZone" should "add a zone id parameter to a request" in {
    val zoneId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ZONE_ID, zoneId))
    val request = new VmFindRequest

    assert(request.withZone(zoneId).request.getParameters.asScala.toSet == expectedParameters)
  }
}
