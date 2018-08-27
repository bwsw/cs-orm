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
package com.bwsw.cloudstack.entities.events

import java.time.OffsetDateTime
import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{FlatSpec, Matchers}

class UserEventsRetrievingTest
  extends FlatSpec
    with TestEntities
    with Matchers {

  val userId: UUID = UUID.randomUUID()
  val sleepInterval = 5000
  val pollTimeout = 1000
  val userCreationSettings = UserCreateRequest.Settings(
    accountName = adminAccount,
    email = "e@e",
    firstName = "first",
    lastName = "last",
    password = "passwd",
    username = s"username $userId"
  )

  val userCreateRequest = new UserCreateRequest(userCreationSettings)
  userCreateRequest.withId(userId)

  val consumer = new Consumer(kafkaEndpoint, kafkaTopic)
  consumer.assignToEnd()

  private val beforeCreation = OffsetDateTime.now().minusSeconds(1)
  executor.executeRequest(userCreateRequest.getRequest)

  Thread.sleep(sleepInterval)

  val records: List[String] = consumer.poll(pollTimeout)

  it should "retrieve UserCreateEvent with status 'Completed' from Kafka records" in {
    val afterCreation = OffsetDateTime.now()
    val actualUserCreateEvents = records.map(RecordToEventDeserializer.deserializeRecord).filter {
      case UserCreateEvent(Constants.Statuses.COMPLETED, `userId`, dateTime) =>
        dateTime.isAfter(beforeCreation) && dateTime.isBefore(afterCreation)
      case _ => false
    }

    actualUserCreateEvents.length shouldBe 1
  }
}
