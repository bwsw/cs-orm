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
package com.bwsw.cloudstack

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser
import org.slf4j.LoggerFactory

/**
  * Class is responsible for ApacheCloudStackClient creation using the key authentication mechanism
  *
  * @param settings see: KeyAuthenticationClientCreator.Settings
  */
class KeyAuthenticationClientCreator(settings: KeyAuthenticationClientCreator.Settings) extends ClientCreator {
  private val logger = LoggerFactory.getLogger(this.getClass)
  protected[cloudstack] val apacheCloudStackUser = new ApacheCloudStackUser(settings.secretKey, settings.apiKey)

  override def createClient(endpoint: String): ApacheCloudStackClient = {
    logger.trace(s"createClient(endpoint: $endpoint)")
    new ApacheCloudStackClient(endpoint, apacheCloudStackUser)
  }
}

object KeyAuthenticationClientCreator {
  /**
    * Class is responsible for providing authentication keys settings for ApacheCloudStackClient creation
    *
    * @param secretKey secret key of user for authorization on CloudStack server
    * @param apiKey api key of user for authorization on CloudStack server
    */
  case class Settings(secretKey: String, apiKey: String)
}
