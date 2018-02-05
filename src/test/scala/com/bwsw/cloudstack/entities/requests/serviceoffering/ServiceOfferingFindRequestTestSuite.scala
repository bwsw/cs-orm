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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterKeys, ParameterValues}
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class ServiceOfferingFindRequestTestSuite extends FlatSpec {
  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(ParameterKeys.RESPONSE, ParameterValues.JSON),
    new ApacheCloudStackApiCommandParameter(ParameterKeys.LIST_ALL, true)
  )

  it should "create a request with predefined parameters" in {
    val request = new ServiceOfferingFindRequest

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
    assert(request.request.getCommand == Commands.LIST_SERVICE_OFFERINGS)
  }
}
