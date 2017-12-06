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
package com.bwsw.cloudstack.entities

import java.net.NoRouteToHostException

import br.com.autonomiccs.apacheCloudStack.client.{ApacheCloudStackClient, ApacheCloudStackRequest}
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRuntimeException
import com.bwsw.cloudstack.entities.common.WeightedQueue
import com.bwsw.cloudstack.entities.common.traits.Queue
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class Executor(settings: Executor.Settings,
               waitIfServerUnavailable: Boolean = true){
  private val logger = LoggerFactory.getLogger(this.getClass)

  protected val apacheCloudStackUser = new ApacheCloudStackUser(settings.secretKey, settings.apiKey)
  protected val endpointQueue: Queue[String] = new WeightedQueue[String](settings.endpoints.toList)

  def executeRequest(request: ApacheCloudStackRequest): String = {
    logger.trace(s"executeRequest(request: $request)")

    if (waitIfServerUnavailable) {
      tryExecuteRequest(request)
    } else {
      val client = createClient(endpointQueue.getElement)
      client.executeRequest(request)
    }
  }

  protected def createClient(endpoint: String): ApacheCloudStackClient = {
    new ApacheCloudStackClient(endpoint, apacheCloudStackUser)
  }

  protected def tryExecuteRequest(request: ApacheCloudStackRequest): String = {
    logger.trace(s"tryExecuteRequest(request: $request)")
    val endpoint = endpointQueue.getElement
    val client = createClient(endpoint)
    Try {
      client.executeRequest(request)
    } match {
      case Success(x) => x
      case Failure(e: ApacheCloudStackClientRuntimeException) if e.getCause.isInstanceOf[NoRouteToHostException] =>
        logger.warn(s"CloudStack server is unavailable by endpoint: $endpoint, retry execute request after: ${settings.retryDelay}")
        Thread.sleep(settings.retryDelay)
        endpointQueue.moveElementToEnd(endpoint)
        tryExecuteRequest(request)
      case Failure(e: Throwable) =>
        logger.warn(s"Request execution threw an exception: $e")
        throw e
    }
  }
}

object Executor {
  case class Settings(endpoints: Array[String], secretKey: String, apiKey: String, retryDelay: Int)
}