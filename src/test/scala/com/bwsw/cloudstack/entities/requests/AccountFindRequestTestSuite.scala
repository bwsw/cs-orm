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
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class AccountFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter("response","json"),
    new ApacheCloudStackApiCommandParameter("listAll","true")
  )

  it should "create a request with predefined parameters" in {
    val request = new AccountFindRequest

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }

  "withId" should "add id parameter to a request" in {
    val accountId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("id", accountId))
    val request = new AccountFindRequest

    assert(request.withId(accountId).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withName" should "add account name parameter to a request" in {
    val accountName = "test"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("name", accountName))
    val request = new AccountFindRequest

    assert(request.withName(accountName).request.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add account name parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter("domainid", domainId))
    val request = new AccountFindRequest

    assert(request.withDomain(domainId).request.getParameters.asScala.toSet == expectedParameters)
  }
}
