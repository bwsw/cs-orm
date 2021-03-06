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

object Constants {

  object Events {
    final val USER_CREATE = "USER.CREATE"
    final val ACCOUNT_CREATE = "ACCOUNT.CREATE"
    final val ACCOUNT_DELETE = "ACCOUNT.DELETE"
    final val VM_CREATE = "VM.CREATE"
    final val VM_DESTROY = "VM.DESTROY"
  }

  object Statuses {
    val COMPLETED: String = "Completed"
    val SCHEDULED: String = "Scheduled"
    val SUCCEEDED: String = "SUCCEEDED"
  }

  object FieldNames {
    val Event: String = "event"
    val CommandEventType: String = "commandEventType"
    val EventDateTime: String = "eventDateTime"
    val EntityUuid: String = "entityuuid"
    val Description: String = "description"
    val Status: String = "status"
  }

}
