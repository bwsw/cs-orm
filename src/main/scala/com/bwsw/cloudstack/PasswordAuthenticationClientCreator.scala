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

/**
  * Class is responsible for ApacheCloudStackClient creation
  *
  * @param settings see: PasswordAuthenticationClientCreator.Settings
  */
class PasswordAuthenticationClientCreator(settings: PasswordAuthenticationClientCreator.Settings) extends ClientCreator {
  private[cloudstack] val apacheCloudStackUser = new ApacheCloudStackUser(settings.username, settings.password, settings.domain)

  override def createClient(endpoint: String): ApacheCloudStackClient = {
    new ApacheCloudStackClient(endpoint, apacheCloudStackUser)
  }
}

object PasswordAuthenticationClientCreator {
  /**
    * Class is responsible for providing settings for ApacheCloudStackClient creation
    *
    * @param username username for authorization on CloudStack server
    * @param password password for authorization on CloudStack server
    * @param domain user domain for authorization. Use "/" for ROOT domain
    */
  case class Settings(username: String, password: String, domain: String)
}
