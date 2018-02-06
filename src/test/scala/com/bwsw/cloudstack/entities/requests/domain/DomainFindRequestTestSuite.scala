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
package com.bwsw.cloudstack.entities.requests.domain

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class DomainFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE, ParameterValues.JSON)
  )

  it should "create a request with predefined parameters" in {
    val request = new DomainFindRequest

    assert(request.getRequest.getParameters.asScala.toSet == defaultParameters)
    assert(request.getRequest.getCommand == Commands.LIST_DOMAINS)
  }

  it should "create child DomainFindRequest with one new parameter" in {
    val testParameterValue = "testValue"
    val testParameterName = "testName"

    val expectedParameters = defaultParameters ++ Set(
      new ApacheCloudStackApiCommandParameter(testParameterName, testParameterValue)
    )

    class TestDomainFindRequest extends DomainFindRequest {
      def withTestParameter(value: String): Unit = {
        addParameter(testParameterName, value)
      }
    }

    val request = new TestDomainFindRequest
    request.withTestParameter(testParameterValue)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }
}
