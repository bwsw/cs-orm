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

import spray.json._

/**
  * @author Pavel Tomskikh
  */
final case class JobResult[T](path: String,
                              result: Either[String, T])


object JobResult {

  implicit def jobResultJsonFormat[T](implicit resultFormat: JsonFormat[T]): RootJsonFormat[JobResult[T]] =
    new RootJsonFormat[JobResult[T]] {
      override def read(json: JsValue): JobResult[T] = json match {
        case JsString(string) =>
          val (path, result) = string.span(_ != '{')

          val maybeResult = try {
            Right(result.parseJson.convertTo[T])
          } catch {
            case _: Exception => Left(result)
          }

          JobResult(path, maybeResult)

        case _ => deserializationError(s"Expected JobResult as JsString, but got $json")
      }

      override def write(jobResult: JobResult[T]): JsValue = {
        val result = jobResult.result match {
          case Right(validResult) => validResult.toJson
          case Left(str) => JsString(str)
        }

        JsString(s"${jobResult.path}$result")
      }
    }
}

