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

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Constants
import com.bwsw.cloudstack.entities.responses.domain.DomainFindResponse
import org.scalatest.FlatSpec

class DomainCreateRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "create a domain using a request which contains only required parameters" in {
    val expectedDomainName = UUID.randomUUID().toString

    val request = new DomainCreateRequest(expectedDomainName).getRequest

    executor.executeRequest(request)

    checkDomainCreation(expectedDomainName)
  }

  it should "create a domain using a request which contains only required parameters and a parameter with incorrect key" in {
    val expectedDomainName = UUID.randomUUID().toString
    val incorrectParameterName = UUID.randomUUID().toString

    val domainCreateRequest = new DomainCreateRequest(expectedDomainName)
    domainCreateRequest.addParameter(incorrectParameterName, "value")

    executor.executeRequest(domainCreateRequest.getRequest)

    checkDomainCreation(expectedDomainName)
  }

  private def checkDomainCreation(expectedDomainName: String): Unit = {
    class TestDomainFindRequest extends DomainFindRequest {
      def listAll(): Unit = {
        addParameter(Constants.ParameterKeys.LIST_ALL, true)
      }

      def withName(name: String): Unit = {
        addParameter(Constants.ParameterKeys.NAME, name)
      }
    }

    val findRequest = new TestDomainFindRequest
    findRequest.listAll()
    findRequest.withName(expectedDomainName)

    val actualDomainName = mapper.deserialize[DomainFindResponse](
      executor.executeRequest(findRequest.getRequest)
    ).entityList.entities.get.head.name

    assert(actualDomainName == expectedDomainName)
  }
}
