<!--
  - Licensed to the Apache Software Foundation (ASF) under one
  - or more contributor license agreements.  See the NOTICE file
  - distributed with this work for additional information
  - regarding copyright ownership.  The ASF licenses this file
  - to you under the Apache License, Version 2.0 (the
  - "License"); you may not use this file except in compliance
  - with the License.  You may obtain a copy of the License at
  -
  -    https://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing,
  - software distributed under the License is distributed on an
  - "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  - KIND, either express or implied.  See the License for the
  - specific language governing permissions and limitations
  - under the License.
  -->

<script lang="ts" setup>
  import { computed, ref, reactive, watch, toRefs } from 'vue'
  import { State } from '@/api/job/types'
  import {
    MinusCircleFilled as Canceled,
    CheckCircleFilled as Successful,
    CloseCircleFilled as Failed
  } from '@ant-design/icons-vue'

  interface ProgressItem {
    state: keyof typeof State
    [property: string]: any
  }

  interface ProgressProps {
    state: keyof typeof State
    progressData: ProgressItem[]
  }

  interface ProgressConfig {
    progress: number
    status: string
  }

  const props = withDefaults(defineProps<ProgressProps>(), {})

  const progressConfig = reactive<ProgressConfig>({
    progress: 0,
    status: 'status'
  })

  const { progress, status } = toRefs(progressConfig)

  const data = ref(computed(() => props.progressData))
  const icon = ref(Canceled)

  watch(
    () => props.state,
    (val) => {
      if (val === 'Pending') {
        Object.assign(progressConfig, {
          progress: 0,
          status: 'normal'
        })
      }
      if (val === 'Successful') {
        Object.assign(progressConfig, {
          progress: 100,
          status: 'success'
        })
        icon.value = Successful
      }
      if (val === 'Processing') {
        const proportion =
          data.value.filter((v: ProgressItem) => v.state === 'Successful')
            .length + 1
        Object.assign(progressConfig, {
          progress: (proportion / data.value.length) * 100,
          status: 'active'
        })
      }
      if (val === 'Canceled') {
        Object.assign(progressConfig, {
          progress: 100,
          status: 'normal'
        })
        icon.value = Canceled
      }
      if (val === 'Failed') {
        Object.assign(progressConfig, {
          progress: 100,
          status: 'exception'
        })
        icon.value = Failed
      }
    },
    {
      immediate: true
    }
  )
</script>

<template>
  <div>
    <a-progress
      :percent="progress"
      :status="status"
      :stroke-color="State[props.state]"
    >
      <template #format="percent">
        <span v-if="percent < 100">{{ percent }}</span>
        <component :is="icon" :style="{ color: State[props.state] }" />
      </template>
    </a-progress>
  </div>
</template>

<style lang="scss" scoped></style>
