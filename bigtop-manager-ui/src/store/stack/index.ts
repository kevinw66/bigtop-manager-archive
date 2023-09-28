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

import { defineStore } from 'pinia'
import { list } from '@/api/stack'
import { shallowReactive } from 'vue'
import { StackOptionProps } from '@/store/stack/types.ts'
import { StackVO } from '@/api/stack/types.ts'

export const useStackStore = defineStore(
  'stack',
  () => {
    // const stackServiceVOList = shallowReactive<StackServiceVO[]>([])
    const stackOptions = shallowReactive<StackOptionProps[]>([])

    const initStacks = async () => {
      const stacks: StackOptionProps[] = []
      const stackVOList: StackVO[] = await list()
      stackVOList.forEach((stackVO) => {
        const props: StackOptionProps = {
          label: stackVO.stackVersion,
          value: stackVO.stackVersion
        }

        const existStackVO = stacks.find(
          (stack) => stack.label === stackVO.stackName
        )

        if (!existStackVO) {
          stacks.unshift({
            label: stackVO.stackName,
            value: stackVO.stackName,
            children: [props]
          })
        } else {
          existStackVO.children?.unshift(props)
        }
      })

      return Promise.resolve(stacks)
    }

    const getStacks = async () => {
      Object.assign(stackOptions, await initStacks())
      return Promise.resolve()
    }

    return {
      stackOptions,
      getStacks
    }
  },
  { persist: false }
)
