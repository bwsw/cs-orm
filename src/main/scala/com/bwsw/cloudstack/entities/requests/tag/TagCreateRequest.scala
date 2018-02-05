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
package com.bwsw.cloudstack.entities.requests.tag

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.tag.types.TagType
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.Request
import com.bwsw.cloudstack.entities.responses.Tag

/**
  * Class is responsible for building ApacheCloudStackRequest with specified parameters for creating tags
  *
  * @param settings required parameters for tag list creation, more info see AccountCreateRequest.Settings
  */
class TagCreateRequest(settings: TagCreateRequest.Settings) extends Request {
  override protected val request = new ApacheCloudStackRequest(Commands.CREATE_TAGS)
    .addParameter(RESPONSE, ParameterValues.JSON)
    .addParameter(RESOURCE_TYPE, settings.resourceType.name)
    .addParameter(RESOURCE_IDS, settings.resourceIds.mkString(","))

  private var i = 0
  settings.tags.foreach { tag =>
    request.addParameter(s"tags[$i].key", tag.key)
    request.addParameter(s"tags[$i].value", tag.value)
    i = i + 1
  }
}

object TagCreateRequest {
  /**
    * Class is responsible for providing tags creation settings.
    *
    * @param resourceType type of creating tags
    * @param resourceIds a set of resource's UUIDs for which tags will be created
    * @param tags tag list for creation
    */
  case class Settings(resourceType: TagType, resourceIds: Set[UUID], tags: List[Tag])
}
