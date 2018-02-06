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
package com.bwsw.cloudstack.entities.requests.serviceoffering

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Constants
import com.bwsw.cloudstack.entities.responses.serviceoffering.{ServiceOfferingFindResponse, ServiceOfferingList}
import com.bwsw.cloudstack.entities.util.requests.RequestExecutionHandler
import org.scalatest.FlatSpec

class ServiceOfferingFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "retrieve json string if request contains only default parameters" in {
    val serviceFindRequest = new ServiceOfferingFindRequest
    val response = mapper.deserialize[ServiceOfferingFindResponse](executor.executeRequest(serviceFindRequest.getRequest))

    assert(response.entityList.isInstanceOf[ServiceOfferingList])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431 " +
    "if entity with a specified value of id parameter does not exist" in {
    val serviceId = UUID.randomUUID()
    val serviceFindRequest = new ServiceOfferingFindRequest {
      def withId(id: UUID): Unit = {
        addParameter(Constants.ParameterKeys.ID, id)
      }
    }
    serviceFindRequest.withId(serviceId)

    assert(RequestExecutionHandler.doesEntityNotExist(serviceFindRequest))
  }

  it should "return an empty list of serviceOfferings if entity with a specified value of name parameter does not exist" in {
    val serviceName = UUID.randomUUID().toString
    val serviceFindRequest = new ServiceOfferingFindRequest {
      def withName(name: String): Unit = {
        addParameter(Constants.ParameterKeys.NAME, name)
      }
    }
    serviceFindRequest.withName(serviceName)
    val response = mapper.deserialize[ServiceOfferingFindResponse](executor.executeRequest(serviceFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "retrieve json string if request contains default parameters and parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new ServiceOfferingFindRequest().getRequest.addParameter(incorrectParameterKey, "value")
    val response = mapper.deserialize[ServiceOfferingFindResponse](executor.executeRequest(request))

    assert(response.entityList.isInstanceOf[ServiceOfferingList])
  }
}
