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

import scala.reflect._

import com.bwsw.cloudstack.entities.common.traits.Mapper
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.LoggerFactory

/**
  * Class is responsible for conversion from entities to json string and from json string to entities
  */
class JsonMapper extends Mapper[String] {
  def this(ignoreUnknownProperties: Boolean) = {
    this()
    this.setIgnoreUnknownPropertiesFlag(ignoreUnknownProperties)
  }

  private val logger = LoggerFactory.getLogger(this.getClass)

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

  /**
    * Converts from entity to json string.
    *
    * @param value entity for conversion
    */
  def serialize(value: Any): String = {
    import java.io.StringWriter

    logger.debug(s"Serialize a value of class: '${value.getClass}' to string")
    val writer = new StringWriter()
    mapper.writeValue(writer, value)
    writer.toString
  }

  /**
    * Converts from json string to entity.
    *
    * @param value json string for conversion
    */
  def deserialize[T: Manifest](value: String): T = {
    logger.debug(s"Deserialize a value: '$value' to object")
    mapper.readValue(value, typeReference[T])
  }

  private def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = classTag[T].runtimeClass
  }

  def setIgnoreUnknownPropertiesFlag(ignoreUnknownProperties: Boolean): Unit = {
    logger.debug(s"Set a value of flag: FAIL_ON_UNKNOWN_PROPERTIES to '$ignoreUnknownProperties'")
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreUnknownProperties)
  }

  def getIgnoreUnknownPropertiesFlag: Boolean = {
    logger.debug(s"Retrieve a value of flag: FAIL_ON_UNKNOWN_PROPERTIES")
    !((mapper.getDeserializationConfig.getDeserializationFeatures & DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask) == DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask)
  }
}
