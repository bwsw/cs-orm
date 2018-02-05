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

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.requests.user.{UserCreateRequest, UserFindRequest}
import com.bwsw.cloudstack.entities.responses.user.User
import org.scalatest.FlatSpec

class UserDaoTestSuite extends FlatSpec with TestData {
  val findRequest = new UserFindRequest

  "find" should "return non-empty entity list if a response json string contains the relevant data" in {
    val expectedUserList = List(testUser)

    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        Response.getUserResponseJson(testUser)
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assert(userDao.find(findRequest) == expectedUserList)
  }

  "find" should "return an empty entity list if a response json string does not contain the relevant data" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        Response.getResponseWithEmptyUserList
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assert(userDao.find(findRequest) == List.empty[User])
  }

  "find" should "return an empty entity list if Executor throws ApacheCloudStackClientRequestRuntimeException" +
    " with a status 431" in {
    val statusCode = 431
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assert(userDao.find(findRequest) == List.empty[User])
  }

  "find" should "not swallow non-ApacheCloudStackClientRequestRuntimeException" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new Exception
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assertThrows[Exception](userDao.find(findRequest))
  }

  "find" should "not swallow ApacheCloudStackClientRequestRuntimeException with a status different from 431" in {
    val statusCode = 400
    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.request == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assertThrows[ApacheCloudStackClientRequestRuntimeException](userDao.find(findRequest))
  }

  "create" should "submit request to Executor" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new UserCreateRequest(UserCreateRequest.Settings(
      accountName = "account",
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "passw",
      username = "user"
    ))

    val expectedRequests = List(createRequest.request)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        ""
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assert(userDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }

  "create" should "not swallow an exception" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new UserCreateRequest(UserCreateRequest.Settings(
      accountName = "account",
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "passw",
      username = "user"
    ))

    val expectedRequests = List(createRequest.request)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        throw new Exception
      }
    }

    val userDao = new UserDao(executor, jsonMapper)

    assertThrows[Exception](userDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }
}
