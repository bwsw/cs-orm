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
import java.util.Random

import br.com.autonomiccs.apacheCloudStack.client.{ApacheCloudStackClient, ApacheCloudStackRequest}
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRuntimeException
import com.bwsw.cloudstack.entities.common.WeightedQueue
import org.scalatest.{Outcome, PrivateMethodTester, fixture}

import scala.util.{Failure, Try}

class ExecutorTestSuite extends fixture.FlatSpec with PrivateMethodTester {
  val firstEndpoint = "http://127.0.0.1:8080/client/api"
  val secondEndpoint = "http://127.0.0.2:8080/client/api"
  val expectedRequest = new ApacheCloudStackRequest("test")
  val expectedResponse = "response"

  val settings = Executor.Settings(Array(firstEndpoint, secondEndpoint), "secretKey", "apiKey", retryDelay = 100)

  case class FixtureParam(queue: WeightedQueue[String])

  def withFixture(test: OneArgTest): Outcome = {
    val endpointQueue = new WeightedQueue[String](settings.endpoints.toList) {
      override val r = new Random {
        override def nextInt(n: Int): Int = 0
      }
    }
    val theFixture = FixtureParam(endpointQueue)

    withFixture(test.toNoArgTest(theFixture))
  }

  "tryExecuteRequest" should "return response" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint, secondEndpoint)

    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val executor = new Executor(settings, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def createClient(endpoint: String): ApacheCloudStackClient = {
        new ApacheCloudStackClient(firstEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              expectedResponse
          }
        }
      }

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assert(tryExecuteRequestTest == expectedResponse)
  }

  "tryExecuteRequest" should "change apacheCloudStackClient after NoRouteToHostException" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint, secondEndpoint)

    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val executor = new Executor(settings, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def createClient(endpoint: String): ApacheCloudStackClient = {
        endpoint match {
          case `firstEndpoint` => new ApacheCloudStackClient(firstEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              checkedEndpoints = checkedEndpoints ::: firstEndpoint :: Nil
              throw new ApacheCloudStackClientRuntimeException(new NoRouteToHostException)
            }
          }
          case `secondEndpoint` => new ApacheCloudStackClient(secondEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              checkedEndpoints = checkedEndpoints ::: secondEndpoint :: Nil
              expectedResponse
            }
          }
        }
      }

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assert(tryExecuteRequestTest == expectedResponse)

    assert(checkedEndpoints == expectedEndpoints)
  }

  "tryExecuteRequest" should "not swallow non-NoRouteToHostException" in { fixture =>
    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val executor = new Executor(settings, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def createClient(endpoint: String): ApacheCloudStackClient = {
        endpoint match {
          case `firstEndpoint` => new ApacheCloudStackClient(firstEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              throw new Exception
            }
          }
          case `secondEndpoint` => new ApacheCloudStackClient(secondEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              expectedResponse
            }
          }
        }
      }

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assertThrows[Exception]{
      tryExecuteRequestTest()
    }
  }

  "executeRequest" should "return response if waitIfServerUnavailable flag is true" in { fixture =>
    val executor = {
      new Executor(settings, waitIfServerUnavailable = true) {
        override val endpointQueue = fixture.queue

        override def createClient(endpoint: String): ApacheCloudStackClient = {
          assert(endpoint == endpointQueue.getElement)
          new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              assert(request.toString == expectedRequest.toString, "request is wrong")
              expectedResponse
            }
          }
        }
      }
    }

    assert(executor.executeRequest(expectedRequest) == expectedResponse)
  }

  "executeRequest" should "return response if waitIfServerUnavailable flag is false" in { fixture =>
    val executor = {
      new Executor(settings, waitIfServerUnavailable = false) {
        override val endpointQueue = fixture.queue

        override def createClient(endpoint: String): ApacheCloudStackClient = {
          assert(endpoint == endpointQueue.getElement)
          new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              assert(request.toString == expectedRequest.toString, "request is wrong")
              expectedResponse
            }
          }
        }
      }
    }

    assert(executor.executeRequest(expectedRequest) == expectedResponse)
  }

  "executeRequest" should "throw ApacheCloudStackClientRuntimeException(NoRouteToHostException) " +
    "if CloudStack server is unavailable and waitIfServerUnavailable flag is false" in { fixture =>
    val expectedRequest = new ApacheCloudStackRequest("test")
    val expectedResponse = "response"

    val executor = {
      new Executor(settings, waitIfServerUnavailable = false) {
        override val endpointQueue = fixture.queue

        override def createClient(endpoint: String): ApacheCloudStackClient = {
          assert(endpoint == endpointQueue.getElement)
          new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              assert(request.toString == expectedRequest.toString, "request is wrong")
              throw new ApacheCloudStackClientRuntimeException(new NoRouteToHostException)
            }
          }
        }
      }
    }

    val result = Try {
      executor.executeRequest(expectedRequest) == expectedResponse
    } match {
      case Failure(e: ApacheCloudStackClientRuntimeException) => e.getCause.isInstanceOf[NoRouteToHostException]
      case _ => false
    }

    assert(result)
  }
}
