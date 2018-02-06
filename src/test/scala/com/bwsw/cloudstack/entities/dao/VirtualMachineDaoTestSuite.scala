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
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmFindRequest}
import com.bwsw.cloudstack.entities.responses.VirtualMachine
import org.scalatest.FlatSpec

class VirtualMachineDaoTestSuite extends FlatSpec with TestData {
  val findRequest = new VmFindRequest

  "find" should "return non-empty entity list if a response json string contains the relevant data" in {
    val vmId = UUID.randomUUID()
    val accountName = "test"
    val domainId = UUID.randomUUID()
    val expectedVmList = List(VirtualMachine(vmId, accountName, domainId))

    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        Response.getVmResponseJson(vmId.toString, accountName, domainId.toString)
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assert(vmDao.find(findRequest) == expectedVmList)
  }

  "find" should "return an empty entity list if a response json string does not contain the relevant data" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        Response.getResponseWithEmptyVmList
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assert(vmDao.find(findRequest) == List.empty[VirtualMachine])
  }

  "find" should "return an empty entity list if Executor throws ApacheCloudStackClientRequestRuntimeException" +
    " with a status 431" in {
    val statusCode = 431
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assert(vmDao.find(findRequest) == List.empty[VirtualMachine])
  }

  "find" should "not swallow non-ApacheCloudStackClientRequestRuntimeException" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new Exception
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assertThrows[Exception](vmDao.find(findRequest))
  }

  "find" should "not swallow ApacheCloudStackClientRequestRuntimeException with a status different from 431" in {
    val statusCode = 400
    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assertThrows[ApacheCloudStackClientRequestRuntimeException](vmDao.find(findRequest))
  }


  "create" should "submit request to Executor" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new VmCreateRequest(VmCreateRequest.Settings(
      serviceOfferingId = UUID.randomUUID(),
      templateId = UUID.randomUUID(),
      zoneId = UUID.randomUUID()
    ))

    val expectedRequests = List(createRequest.getRequest)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        ""
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assert(vmDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }

  "create" should "not swallow an exception" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new VmCreateRequest(VmCreateRequest.Settings(
      serviceOfferingId = UUID.randomUUID(),
      templateId = UUID.randomUUID(),
      zoneId = UUID.randomUUID()
    ))

    val expectedRequests = List(createRequest.getRequest)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        throw new Exception
      }
    }

    val vmDao = new VirtualMachineDao(executor, jsonMapper)

    assertThrows[Exception](vmDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }
}
