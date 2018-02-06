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

import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Request
import com.bwsw.cloudstack.entities.requests.tag.types.TagType
import com.bwsw.cloudstack.entities.responses.tag.TagFindResponse
import com.bwsw.cloudstack.entities.util.requests.IntegrationTestConstants.ParameterValues
import org.scalatest.FlatSpec

import scala.util.{Failure, Success, Try}

class TagFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {
  it should "retrieve json string if request contains only default parameters" in {
    val tagFindRequest = new TagFindRequest
    val response = mapper.deserialize[TagFindResponse](executor.executeRequest(tagFindRequest.getRequest))

    assert(response.isInstanceOf[TagFindResponse])
  }

  it should "return an empty list of tags if entity with a specified value of resource parameter does not exist" in {
    val resourceId = UUID.randomUUID()
    val tagFindRequest = new TagFindRequest().withResource(resourceId)

    checkEmptyResponse(tagFindRequest)
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of domain parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val tagFindRequest = new TagFindRequest().withDomain(domainId)

    tryExecuteRequest(tagFindRequest)
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431" +
    " if entity with a specified value of account name parameter does not exist" in {
    val accountName = UUID.randomUUID().toString
    val tagFindRequest = new TagFindRequest().withAccountName(accountName)

    tryExecuteRequest(tagFindRequest)
  }

  it should "return an empty list of tags if entity with a specified value of key parameter does not exist" in {
    val key = UUID.randomUUID().toString
    val tagFindRequest = new TagFindRequest().withKey(key)

    checkEmptyResponse(tagFindRequest)
  }

  it should "return an empty list of tags if entity with a specified value of value parameter does not exist" in {
    val value = UUID.randomUUID().toString
    val tagFindRequest = new TagFindRequest().withValue(value)

    checkEmptyResponse(tagFindRequest)
  }

  it should "return an empty list of tags if entity with a specified value of resource type parameter does not exist" in {
    case object IncorrectTagType extends TagType(UUID.randomUUID().toString)

    val tagFindRequest = new TagFindRequest().withResourceType(IncorrectTagType)

    checkEmptyResponse(tagFindRequest)
  }

  it should "retrieve json string if request contains default parameters and parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new TagFindRequest().getRequest.addParameter(incorrectParameterKey, ParameterValues.DUMMY_VALUE)
    val response = mapper.deserialize[TagFindResponse](executor.executeRequest(request))

    assert(response.isInstanceOf[TagFindResponse])
  }

  private def checkEmptyResponse(request: Request) = {
    val response = mapper.deserialize[TagFindResponse](executor.executeRequest(request.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  private def tryExecuteRequest(request: Request): Boolean = {
    Try {
      executor.executeRequest(request.getRequest)
    } match {
      case Success(_) => false
      case Failure(e: ApacheCloudStackClientRequestRuntimeException) =>
        e.getStatusCode == 431
      case Failure(_: Throwable) => false
    }
  }
}
