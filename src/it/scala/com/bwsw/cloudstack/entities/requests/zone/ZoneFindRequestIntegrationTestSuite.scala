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
package com.bwsw.cloudstack.entities.requests.zone

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Constants
import com.bwsw.cloudstack.entities.responses.zone.{ZoneFindResponse, ZoneList}
import com.bwsw.cloudstack.entities.util.requests.RequestExecutionHandler
import org.scalatest.FlatSpec

class ZoneFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "retrieve json string if request contains only default parameters" in {
    val zoneFindRequest = new ZoneFindRequest
    val response = mapper.deserialize[ZoneFindResponse](executor.executeRequest(zoneFindRequest.getRequest))

    assert(response.entityList.isInstanceOf[ZoneList])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431 " +
    "if entity with a specified value of id parameter does not exist" in {
    val zoneId = UUID.randomUUID()
    val zoneFindRequest = new ZoneFindRequest {
      def withId(id: UUID): Unit = {
        addParameter(Constants.ParameterKeys.ID, id)
      }
    }
    zoneFindRequest.withId(zoneId)

    assert(RequestExecutionHandler.doesEntityNotExist(zoneFindRequest))
  }

  it should "return an empty list of zones if entity with a specified value of name parameter does not exist" in {
    val zoneName = UUID.randomUUID().toString
    val zoneFindRequest = new ZoneFindRequest {
      def withName(name: String): Unit = {
        addParameter(Constants.ParameterKeys.NAME, name)
      }
    }
    zoneFindRequest.withName(zoneName)
    val response = mapper.deserialize[ZoneFindResponse](executor.executeRequest(zoneFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "retrieve json string if request contains default parameters and parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new ZoneFindRequest().getRequest.addParameter(incorrectParameterKey, "value")
    val response = mapper.deserialize[ZoneFindResponse](executor.executeRequest(request))

    assert(response.entityList.isInstanceOf[ZoneList])
  }
}
