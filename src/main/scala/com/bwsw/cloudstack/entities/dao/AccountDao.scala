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
import com.bwsw.cloudstack.entities.requests.account.{AccountCreateRequest, AccountFindRequest}
import com.bwsw.cloudstack.entities.responses.account.{Account, AccountFindResponse}

import scala.util.{Failure, Success, Try}

/**
  * Class is responsible for creating and retrieving ApacheCloudStack accounts
  *
  * @param executor see: [[Executor]]
  * @param mapper see: [[JsonMapper]]
  */
class AccountDao(executor: Executor, mapper: JsonMapper) extends GenericDao[AccountFindResponse, Account](executor, mapper) {
  override protected type F = AccountFindRequest
  override protected type C = AccountCreateRequest

  /**
    * Create an account
    *
    * @param request see: [[AccountCreateRequest]]
    */
  override def create(request: C): Unit = {
    Try {
      super.create(request)
    } match {
      case Success(x) =>
        logger.debug(s"Account was created by request: $request")
      case Failure(e: Throwable) =>
        logger.error(s"Can't create an account, exception: $e was thrown")
        throw e
    }
  }

  /**
    * Retrieve accounts
    *
    * @param request see: [[AccountFindRequest]]
    */
  override def find(request: F)(implicit m: Manifest[AccountFindResponse]): List[Account] = {
    logger.trace(s"find(request: $request)")
    val accounts = Try {
      super.find(request).toList
    } match {
      case Success(x) =>
        logger.debug(s"Accounts were retrieved: $x")
        x
      case Failure(e: ApacheCloudStackClientRequestRuntimeException) if e.getStatusCode == ENTITY_DOES_NOT_EXIST =>
        logger.warn(s"No accounts were found on request: $request")
        List.empty[Account]
      case Failure(e: Throwable) =>
        logger.error(s"Can't find any accounts by request: $request, exception: $e was thrown")
        throw e
    }
    accounts
  }
}
