/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export interface StackVO {
  stackName: string
  stackVersion: string
  services: StackServiceVO[]
  repos: StackRepoVO[]
}

export interface StackServiceVO {
  serviceName: string
  displayName: string
  serviceDesc: string
  serviceVersion: string
}

export interface StackRepoVO {
  repoId: string
  repoName: string
  baseUrl: string
  os: string
  arch: string
}

export interface StackComponentVO {
  componentName: string
  displayName: string
  category: string
  cardinality: string
  serviceName: string
}
