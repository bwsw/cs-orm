package com.bwsw.cloudstack.entities.common

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

/**
  * @author Pavel Tomskikh
  */
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
