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
package com.bwsw.cloudstack.entities.dao

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.requests.tag.types.UserTagType
import com.bwsw.cloudstack.entities.requests.tag.{TagCreateRequest, TagFindRequest}
import com.bwsw.cloudstack.entities.responses.Tag
import org.scalatest.FlatSpec

class TagDaoTestSuite extends FlatSpec with TestData {
  val findRequest = new TagFindRequest

  "find" should "return non-empty entity set if a response json string contains the relevant data" in {
    val key = "key"
    val value = "value"
    val expectedTagSet = Set(Tag(key, value))

    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        Response.getTagResponseJson(key, value)
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assert(tagDao.find(findRequest) == expectedTagSet)
  }

  "find" should "return an empty entity set if a response json string does not contain the relevant data" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        Response.getResponseWithEmptyTagList
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assert(tagDao.find(findRequest) == Set.empty[Tag])
  }

  "find" should "return an empty entity set if Executor throws ApacheCloudStackClientRequestRuntimeException" +
    " with a status 431" in {
    val statusCode = 431
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assert(tagDao.find(findRequest) == Set.empty[Tag])
  }

  "find" should "not swallow non-ApacheCloudStackClientRequestRuntimeException" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new Exception
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assertThrows[Exception](tagDao.find(findRequest))
  }

  "find" should "not swallow ApacheCloudStackClientRequestRuntimeException with a status different from 431" in {
    val statusCode = 400
    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assertThrows[ApacheCloudStackClientRequestRuntimeException](tagDao.find(findRequest))
  }

  "create" should "submit request to Executor" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new TagCreateRequest(TagCreateRequest.Settings(
      resourceType = new UserTagType,
      resourceIds = Set(UUID.randomUUID(), UUID.randomUUID()),
      tags = List(Tag("key", "value"), Tag("key1", "value1"))
    ))

    val expectedRequests = List(createRequest.request)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        ""
      }
    }

    val tagDao = new TagDao(executor, jsonMapper)

    assert(tagDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }
}
