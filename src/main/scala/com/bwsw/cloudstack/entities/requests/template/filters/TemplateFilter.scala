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
package com.bwsw.cloudstack.entities.requests.template.filters

/**
  * Wrapping for filter constant which is used by TemplateFindRequest for filtering templates while them retrieving
  *
  * @param name filter constant.
  *             possible values are "featured", "self", "selfexecutable","sharedexecutable","executable", and "community".
  *             featured : templates that have been marked as featured and public.
  *             self : templates that have been registered or created by the calling user.
  *             selfexecutable : same as self, but only returns templates that can be used to deploy a new VM.
  *             sharedexecutable : templates ready to be deployed that have been granted to the calling user by another user.
  *             executable : templates that are owned by the calling user, or public templates, that can be used to deploy a VM.
  *             community : templates that have been marked as public but not featured.
  *             all : all templates (only usable by admins).
  */
abstract class TemplateFilter(val name: String)
