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
import org.scalatest.FlatSpec

class PasswordAuthenticationClientCreatorTestSuite extends FlatSpec {

  "createClient" should "create ApacheCloudStackClient which include ApacheCloudStackUser authenticated by keys" in {
    val clientClass = classOf[ApacheCloudStackClient]

    val user = clientClass.getDeclaredField("apacheCloudStackUser")
    user.setAccessible(true)

    val endpoint = "http://localhost:9000"
    val username = "username"
    val password = "pass"
    val domain = "/"
    val settings = PasswordAuthenticationClientCreator.Settings(username, password, domain)

    val client = new PasswordAuthenticationClientCreator(settings).createClient(endpoint)

    val actualUser = user.get(client).asInstanceOf[ApacheCloudStackUser]

    assert(
      Option(actualUser.getApiKey).isEmpty &&
      Option(actualUser.getSecretKey).isEmpty &&
      actualUser.getDomain == domain &&
      actualUser.getPassword == password &&
      actualUser.getUsername == username
    )
  }
}
