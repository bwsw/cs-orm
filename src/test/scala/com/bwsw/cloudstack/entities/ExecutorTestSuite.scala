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
import com.bwsw.cloudstack.KeyAuthenticationClientCreator
import com.bwsw.cloudstack.entities.common.WeightedQueue
import org.scalatest.{Outcome, PrivateMethodTester, fixture}

import scala.util.{Failure, Try}

class ExecutorTestSuite extends fixture.FlatSpec with PrivateMethodTester {
  private val retryDelay = 1000
  val firstEndpoint = "http://127.0.0.1:8080/client/api"
  val secondEndpoint = "http://127.0.0.2:8080/client/api"
  val expectedRequest = new ApacheCloudStackRequest("test")
  val expectedResponse = "response"

  val settings = Executor.Settings(Array(firstEndpoint, secondEndpoint), retryDelay)

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
    val expectedEndpoints = List(firstEndpoint)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")){
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
          override def executeRequest(request: ApacheCloudStackRequest): String = {
            expectedResponse
          }
        }
      }
    }

    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val executor = new Executor(settings, clientCreator, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assert(tryExecuteRequestTest == expectedResponse)
    assert(checkedEndpoints == expectedEndpoints)
  }

  "tryExecuteRequest" should "change apacheCloudStackClient after NoRouteToHostException" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint, secondEndpoint)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")) {
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        endpoint match {
          case `firstEndpoint` => new ApacheCloudStackClient(firstEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              throw new ApacheCloudStackClientRuntimeException(new NoRouteToHostException)
            }
          }
          case `secondEndpoint` => new ApacheCloudStackClient(secondEndpoint, apacheCloudStackUser) {
            override def executeRequest(request: ApacheCloudStackRequest): String = {
              expectedResponse
            }
          }
        }
      }
    }

    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val executor = new Executor(settings, clientCreator, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assert(tryExecuteRequestTest == expectedResponse)

    assert(checkedEndpoints == expectedEndpoints)
  }

  "tryExecuteRequest" should "not swallow non-NoRouteToHostException" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint)
    val tryExecuteRequest = PrivateMethod[String]('tryExecuteRequest)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")) {
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
          override def executeRequest(request: ApacheCloudStackRequest): String = {
            throw new Exception
          }
        }
      }
    }

    val executor = new Executor(settings, clientCreator, waitIfServerUnavailable = true) {
      override val endpointQueue = fixture.queue

      override def tryExecuteRequest(request: ApacheCloudStackRequest): String =
        super.tryExecuteRequest(request)
    }

    def tryExecuteRequestTest(): String = executor invokePrivate tryExecuteRequest(expectedRequest)

    assertThrows[Exception]{
      tryExecuteRequestTest()
    }

    assert(checkedEndpoints == expectedEndpoints)
  }

  "executeRequest" should "return response if waitIfServerUnavailable flag is true" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")) {
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
          override def executeRequest(request: ApacheCloudStackRequest): String = {
            assert(request.toString == expectedRequest.toString, "request is wrong")
            expectedResponse
          }
        }
      }
    }

    val executor = {
      new Executor(settings, clientCreator, waitIfServerUnavailable = true) {
        override val endpointQueue = fixture.queue
      }
    }

    assert(executor.executeRequest(expectedRequest) == expectedResponse)
    assert(checkedEndpoints == expectedEndpoints)
  }

  "executeRequest" should "return response if waitIfServerUnavailable flag is false" in { fixture =>
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")) {
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
          override def executeRequest(request: ApacheCloudStackRequest): String = {
            assert(request.toString == expectedRequest.toString, "request is wrong")
            expectedResponse
          }
        }
      }
    }

    val executor = {
      new Executor(settings, clientCreator, waitIfServerUnavailable = false) {
        override val endpointQueue = fixture.queue
      }
    }

    assert(executor.executeRequest(expectedRequest) == expectedResponse)
    assert(checkedEndpoints == expectedEndpoints)
  }

  "executeRequest" should "throw ApacheCloudStackClientRuntimeException(NoRouteToHostException) " +
    "if CloudStack server is unavailable and waitIfServerUnavailable flag is false" in { fixture =>
    val expectedRequest = new ApacheCloudStackRequest("test")
    val expectedResponse = "response"
    var checkedEndpoints = List.empty[String]
    val expectedEndpoints = List(firstEndpoint)

    val clientCreator = new KeyAuthenticationClientCreator(KeyAuthenticationClientCreator.Settings("secretKey","apiKey")) {
      override def createClient(endpoint: String): ApacheCloudStackClient = {
        checkedEndpoints = checkedEndpoints ::: endpoint :: Nil
        new ApacheCloudStackClient(endpoint, apacheCloudStackUser) {
          override def executeRequest(request: ApacheCloudStackRequest): String = {
            assert(request.toString == expectedRequest.toString, "request is wrong")
            throw new ApacheCloudStackClientRuntimeException(new NoRouteToHostException)
          }
        }
      }
    }

    val executor = {
      new Executor(settings, clientCreator, waitIfServerUnavailable = false) {
        override val endpointQueue = fixture.queue
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
