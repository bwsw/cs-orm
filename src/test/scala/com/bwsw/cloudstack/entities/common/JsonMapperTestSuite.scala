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

import com.fasterxml.jackson.databind.JsonMappingException
import org.scalatest.{FlatSpec, PrivateMethodTester}

class JsonMapperTestSuite extends FlatSpec with PrivateMethodTester {
  val firstValue = "1"
  val secondValue = "2"

  "deserialize" should "deserialize json string to simple case class if setIgnoreUnknownProperties flag is true" +
    "and Json string has equals fields with the class" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\"}"
    val expectedEntity = Test.TestEntity(firstValue, secondValue)
    val jsonMapper = new JsonMapper(true)
    assert(jsonMapper.deserialize[Test.TestEntity](testJson) == expectedEntity)
  }

  "deserialize" should "deserialize json string to simple case class if setIgnoreUnknownProperties flag is true" +
    "and Json string has non equals fields with the class" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\",\"third\":\"3\"}"
    val expectedEntity = Test.TestEntity(firstValue, secondValue)
    val jsonMapper = new JsonMapper(true)

    assert(jsonMapper.deserialize[Test.TestEntity](testJson) == expectedEntity)
  }

  "deserialize" should "deserialize json string to simple case class if setIgnoreUnknownProperties flag is false" +
    "and Json string has equals fields with the class" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\"}"
    val expectedEntity = Test.TestEntity(firstValue, secondValue)
    val jsonMapper = new JsonMapper(false)

    assert(jsonMapper.deserialize[Test.TestEntity](testJson) == expectedEntity)
  }

  "deserialize" should "deserialize json string to parametrized case class if setIgnoreUnknownProperties flag is false" +
    "and Json string has equals fields with the class" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\"}"
    val expectedEntity = Test.ParametrizedTestEntity[String](firstValue, secondValue)
    val jsonMapper = new JsonMapper(false)

    assert(jsonMapper.deserialize[Test.ParametrizedTestEntity[String]](testJson) == expectedEntity)
  }

  "deserialize" should "throw JsonMappingException if setIgnoreUnknownProperties flag is false" +
    "and Json string has non equals fields with the target entity" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\",\"third\":\"3\"}"
    val expectedEntity = Test.TestEntity(firstValue, secondValue)
    val jsonMapper = new JsonMapper(false)

    assertThrows[JsonMappingException](jsonMapper.deserialize[Test.TestEntity](testJson))
  }

  "serialize" should "serialize entity to Json string" in {
    val testJson = "{\"first\":\"" + s"$firstValue" + "\",\"second\":\"" + s"$secondValue" + "\"}"
    val jsonMapper = new JsonMapper()

    assert(jsonMapper.serialize(Test.TestEntity(firstValue, secondValue)) == testJson)
  }

  "getIgnoreUnknownPropertiesFlag" should "get value of IgnoreUnknownProperties flag" in {
    val flag = false
    val jsonMapper = new JsonMapper(flag)
    assert(jsonMapper.getIgnoreUnknownPropertiesFlag == flag)
  }

  "setIgnoreUnknownPropertiesFlag" should "change IgnoreUnknownProperties flag" in {
    val flag = false
    val jsonMapper = new JsonMapper(flag)
    jsonMapper.setIgnoreUnknownPropertiesFlag(!flag)
    assert(jsonMapper.getIgnoreUnknownPropertiesFlag == !flag)
  }
}
