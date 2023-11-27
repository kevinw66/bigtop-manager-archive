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

import { defineStore, storeToRefs } from 'pinia'
import { getStacks } from '@/api/stack'
import { computed, shallowReactive, shallowRef } from 'vue'
import { StackInfo, StackOptionProps } from '@/store/stack/types.ts'
import { StackRepoVO, StackServiceVO, StackVO } from '@/api/stack/types.ts'
import { useClusterStore } from '@/store/cluster'

export const useStackStore = defineStore(
  'stack',
  () => {
    const clusterStore = useClusterStore()
    const { selectedCluster } = storeToRefs(clusterStore)
    const stackOptions = shallowReactive<StackOptionProps[]>([])
    const stackServices = shallowReactive<Record<string, StackServiceVO[]>>({})
    const stackRepos = shallowReactive<Record<string, StackRepoVO[]>>({})
    const initialized = shallowRef(false)

    const currentStack = computed<StackInfo>(() => {
      const cluster = selectedCluster.value
      const name = [cluster?.stackName, cluster?.stackVersion].join('-')
      const services = stackServices[name]

      return {
        name: name,
        services: services ? services : []
      }
    })

    const initStacks = async () => {
      if (!initialized.value) {
        const stackVOList: StackVO[] = await getStacks()
        const stacks: StackOptionProps[] = []
        stackVOList.forEach((stackVO) => {
          const fullStackName = stackVO.stackName + '-' + stackVO.stackVersion
          stackServices[fullStackName] = stackVO.services
          stackRepos[fullStackName] = stackVO.repos

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

        Object.assign(stackOptions, stacks)
        initialized.value = true
      }

      return Promise.resolve()
    }

    return {
      stackOptions,
      stackServices,
      stackRepos,
      currentStack,
      initStacks
    }
  },
  { persist: false }
)
