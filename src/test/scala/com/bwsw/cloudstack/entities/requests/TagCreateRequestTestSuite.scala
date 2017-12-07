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
package com.bwsw.cloudstack.entities.requests

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import org.scalatest.FlatSpec
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.util.UserTagType
import com.bwsw.cloudstack.entities.responses.Tag

import scala.collection.JavaConverters._

class TagCreateRequestTestSuite extends FlatSpec {
  val firstUserId = UUID.randomUUID()
  val secondUserId = UUID.randomUUID()
  val resourceIds = Set(firstUserId, secondUserId)

  val key1 = "key1"
  val key2 = "key2"
  val value1 = "value1"
  val value2 = "value2"
  val tagList = List(Tag("key1", "value1"), Tag("key2","value2"))

  val tagType = new UserTagType()

  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE,"json"),
    new ApacheCloudStackApiCommandParameter(RESOURCE_TYPE, tagType.toString),
    new ApacheCloudStackApiCommandParameter(RESOURCE_IDS, resourceIds.mkString(",")),
    new ApacheCloudStackApiCommandParameter("tags[0].key", key1),
    new ApacheCloudStackApiCommandParameter("tags[0].value", value1),
    new ApacheCloudStackApiCommandParameter("tags[1].key", key2),
    new ApacheCloudStackApiCommandParameter("tags[1].value", value2)
  )

  it should "create a request with predefined parameters" in {
    val request = new TagCreateRequest(TagCreateRequest.Settings(tagType, resourceIds, tagList))

    assert(request.request.getParameters.asScala.toSet == defaultParameters)
  }
}
