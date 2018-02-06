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

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.events.user.UserCreateEvent
import com.bwsw.cloudstack.entities.requests.user.UserCreateRequest
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class UserEventsRetrievingTest extends FlatSpec with TestEntities with BeforeAndAfterAll {
  val userId = UUID.randomUUID()
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

  executor.executeRequest(userCreateRequest.getRequest)

  Thread.sleep(sleepInterval)

  val records = consumer.poll(pollTimeout)

  it should "retrieve UserCreateEvent with status 'Completed' from Kafka records" in {
    val expectedUserCreateEvents = List(UserCreateEvent(Some(Constants.Statuses.COMPLETED), Some(userId)))

    val actualUserCreateEvents = records.map(x => RecordToEventDeserializer.deserializeRecord(x, mapper)).filter {
      case UserCreateEvent(Some(status), Some(entityId))
        if status == Constants.Statuses.COMPLETED && entityId == userId => true
      case _ => false
    }

    assert(actualUserCreateEvents == expectedUserCreateEvents, s"records count: ${records.size}")
  }
}
