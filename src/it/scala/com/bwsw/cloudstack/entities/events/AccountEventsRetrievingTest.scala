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
import com.bwsw.cloudstack.entities.common.DefaultJsonFormats._
import com.bwsw.cloudstack.entities.events.account.{AccountCreateEvent, AccountDeleteEvent}
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.RootAdmin
import com.bwsw.cloudstack.entities.requests.account.{AccountCreateRequest, AccountDeleteRequest}
import com.bwsw.cloudstack.entities.util.events.RecordToEventDeserializer
import com.bwsw.cloudstack.entities.util.kafka.Consumer
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class AccountEventsRetrievingTest
  extends FlatSpec
    with TestEntities
    with BeforeAndAfterAll
    with Matchers {

  val accountId: UUID = UUID.randomUUID()
  val sleepInterval = 10000
  val pollTimeout = 1000
  val accountCreationSettings = AccountCreateRequest.Settings(
    _type = RootAdmin,
    email = "e@e",
    firstName = "first",
    lastName = "last",
    password = "passwd",
    username = s"username $accountId"
  )

  val accountCreateRequest = new AccountCreateRequest(accountCreationSettings)
  accountCreateRequest.withId(accountId)
  val accountDeleteRequest = new AccountDeleteRequest(accountId)

  val consumer = new Consumer(kafkaEndpoint, kafkaTopic)
  consumer.assignToEnd()

  private val beforeCreation = OffsetDateTime.now().minusSeconds(1)
  executor.executeRequest(accountCreateRequest.getRequest)
  private val beforeDeletion = OffsetDateTime.now().minusSeconds(1)
  executor.executeRequest(accountDeleteRequest.getRequest)

  Thread.sleep(sleepInterval)

  val records: List[String] = consumer.poll(pollTimeout)

  it should "retrieve AccountCreateEvent with status 'Completed' from Kafka records" in {
    val afterCreation = OffsetDateTime.now()
    val actualAccountCreateEvents = records.map(RecordToEventDeserializer.deserializeRecord).filter {
      case AccountCreateEvent(Some(Constants.Statuses.COMPLETED), `accountId`, Some(dateTime), _) =>
        dateTime.isAfter(beforeCreation) && dateTime.isBefore(afterCreation)
      case _ => false
    }

    actualAccountCreateEvents.length shouldBe 1
  }

  it should "retrieve AccountDeleteEvent with status 'Completed' from Kafka records" in {
    val afterDeletion = OffsetDateTime.now()
    val actualAccountDeleteEvents = records.map(RecordToEventDeserializer.deserializeRecord).filter {
      case AccountDeleteEvent(Some(Constants.Statuses.COMPLETED), `accountId`, Some(dateTime)) =>
        dateTime.isAfter(beforeDeletion) && dateTime.isBefore(afterDeletion)
      case _ => false
    }

    actualAccountDeleteEvents.length shouldBe 1
  }

  override def afterAll(): Unit = {
    consumer.close()
  }
}
