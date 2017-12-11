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

import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.common.JsonMapper
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmFindRequest}
import com.bwsw.cloudstack.entities.responses.{VirtualMachine, VirtualMachinesResponse}

import scala.util.{Failure, Success, Try}

/**
  * Class is responsible for creating and reading ApacheCloudStack virtual machines
  *
  * @param executor see: [[Executor]]
  * @param mapper see: [[JsonMapper]]
  */
class VirtualMachineDao(executor: Executor, mapper: JsonMapper) extends GenericDao[VirtualMachine, String](executor, mapper) {

  override protected type F = VmFindRequest
  override protected type C = VmCreateRequest

  /**
    * Create virtual machine
    *
    * @param request see: [[VmCreateRequest]]
    */
  override def create(request: C): Unit = {
    Try {
      super.create(request)
    } match {
      case Success(x) =>
        logger.debug(s"Virtual machine was created by request: $request")
      case Failure(e: Throwable) =>
        logger.error(s"Can not to create virtual machine, exception: $e was thrown")
    }
  }

  /**
    * Retrieve virtual machines
    *
    * @param request see: [[VmFindRequest]]
    */
  override def find(request: F): List[VirtualMachine] = {
    logger.trace(s"find(request: $request)")
    val virtualMachines = Try {
      val response = executor.executeRequest(request.request)
      mapper.deserialize[VirtualMachinesResponse](response).entityList.entities.getOrElse(List.empty[VirtualMachine])
    } match {
      case Success(x) =>
        logger.debug(s"Virtual machines were retrieved: $x")
        x
      case Failure(e: ApacheCloudStackClientRequestRuntimeException) if e.getStatusCode == ENTITY_DOES_NOT_EXIST =>
        logger.warn(s"No virtual machines found on request: $request")
        List.empty[VirtualMachine]
      case Failure(e: Throwable) =>
        logger.error(s"Can not to find virtual machines, exception: $e was thrown")
        throw e
    }
    virtualMachines
  }
}
