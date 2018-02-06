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

import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.common.traits.Mapper
import com.bwsw.cloudstack.entities.requests.Request
import com.bwsw.cloudstack.entities.responses.{Entity, EntityResponse}
import org.slf4j.LoggerFactory

abstract class GenericDao[A <: EntityResponse, T <: Entity](protected val executor: Executor, protected val mapper: Mapper[String]) {
  protected val logger = LoggerFactory.getLogger(this.getClass)
  protected val ENTITY_DOES_NOT_EXIST = 431
  protected type F <: Request
  protected type C <: Request

  def create[R <: C](request: R): Unit = {
    logger.trace(s"create(request: $request)")
    executor.executeRequest(request.getRequest)
  }

  def find[R <: F](request: R)(implicit m: Manifest[A]): Iterable[T] = {
    logger.trace(s"find(request: $request)")
    val response = executor.executeRequest(request.getRequest)
    mapper.deserialize[A](response).entityList.entities.getOrElse(List.empty[T]).asInstanceOf[Iterable[T]]
  }
}
