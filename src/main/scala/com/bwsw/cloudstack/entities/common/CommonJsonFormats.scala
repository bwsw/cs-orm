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
package com.bwsw.cloudstack.entities.common

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

object CommonJsonFormats {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][ ]HH:mm:ss[ ]X")

  implicit val uuidJsonFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID = {
      json match {
        case JsString(x) => UUID.fromString(x)
        case x => deserializationError("Expected UUID as JsString, but got " + x)
      }
    }

    override def write(obj: UUID): JsValue = JsString(obj.toString)
  }

  implicit val offsetDateTimeJsonFormat: JsonFormat[OffsetDateTime] = new JsonFormat[OffsetDateTime] {
    override def read(json: JsValue): OffsetDateTime = {
      json match {
        case JsString(x) => OffsetDateTime.parse(x, dateTimeFormatter)
        case x => deserializationError("Expected OffsetDateTime as JsString, but got " + x)
      }
    }

    override def write(obj: OffsetDateTime): JsValue =
      JsString(obj.format(dateTimeFormatter))
  }
}
