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
package com.bwsw.cloudstack.entities.util.requests

import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Request

import scala.util.{Failure, Success, Try}

object RequestExecutionHandler extends TestEntities {
  def entityNotExist(request: Request): Boolean = {
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
