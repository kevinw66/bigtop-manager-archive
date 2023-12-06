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

export const formatFromByte = (value: number): string => {
  if (isNaN(value)) {
    return ''
  }

  if (value < 1024) {
    return `${value} B`
  } else if (value < 1024 ** 2) {
    return `${(value / 1024).toFixed(2)} KB`
  } else if (value < 1024 ** 3) {
    return `${(value / 1024 ** 2).toFixed(2)} MB`
  } else if (value < 1024 ** 4) {
    return `${(value / 1024 ** 3).toFixed(2)} GB`
  } else if (value < 1024 ** 5) {
    return `${(value / 1024 ** 4).toFixed(2)} TB`
  } else {
    return `${(value / 1024 ** 5).toFixed(2)} PB`
  }
}
