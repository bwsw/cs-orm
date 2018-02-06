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
import com.bwsw.cloudstack.entities.requests.tag.{TagCreateRequest, TagFindRequest}
import com.bwsw.cloudstack.entities.responses.{Tag, TagResponse}

import scala.util.{Failure, Success, Try}

/**
  * Class is responsible for creating and retrieving ApacheCloudStack tags
  *
  * @param executor see: [[Executor]]
  * @param mapper see: [[JsonMapper]]
  */
class TagDao(executor: Executor, mapper: JsonMapper) extends GenericDao[TagResponse, Tag](executor, mapper) {
  override protected type F = TagFindRequest
  override protected type C = TagCreateRequest

  /**
    * Create one or more tags
    *
    * @param request see: [[TagCreateRequest]]
    */
  override def create[R <: C](request: R): Unit = {
    Try {
      super.create(request)
    } match {
      case Success(x) =>
        logger.debug(s"Tags were created by request: $request")
      case Failure(e: Throwable) =>
        logger.error(s"Can't create tags, exception: $e was thrown")
        throw e
    }
  }

  /**
    * Retrieve tags
    *
    * @param request see: [[TagFindRequest]]
    */
  override def find[R <: F](request: R)(implicit m: Manifest[TagResponse]): Set[Tag] = {
    logger.trace(s"find(request: $request)")
    val tags = Try {
      super.find(request).toSet
    } match {
      case Success(x) =>
        logger.debug(s"Tags were retrieved: $x")
        x
      case Failure(e: ApacheCloudStackClientRequestRuntimeException) if e.getStatusCode == ENTITY_DOES_NOT_EXIST =>
        logger.warn(s"No tags were found on request: $request")
        Set.empty[Tag]
      case Failure(e: Throwable) =>
        logger.error(s"Can't find any tags by request: $request, exception: $e was thrown")
        throw e
    }
    tags
  }
}
