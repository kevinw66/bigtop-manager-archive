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

  interface ProgressItem {
    state: keyof typeof State
    [property: string]: any
  }

  interface ProgressProps {
    state: keyof typeof State
    progress: ProgressItem[]
  }

  interface ProgressConfig {
    percent: number
    status: string
  }

  const props = withDefaults(defineProps<ProgressProps>(), {})

  const progressConfig = reactive<ProgressConfig>({
    percent: 0,
    status: 'status'
  })

  const { percent, status } = toRefs(progressConfig)

  const data = ref(computed(() => props.progress))

  watch(
    () => props.state,
    (val) => {
      if (val === 'Pending') {
        Object.assign(progressConfig, {
          percent: 0,
          status: 'normal'
        })
      }
      if (val === 'Successful') {
        Object.assign(progressConfig, {
          percent: 100,
          status: 'success'
        })
      }
      if (val === 'Processing') {
        const proportion =
          data.value.filter((v: ProgressItem) => v.state === 'Successful')
            .length + 1
        Object.assign(progressConfig, {
          percent: (proportion / data.value.length) * 100,
          status: 'active'
        })
      }
      if (val === 'Canceled') {
        Object.assign(progressConfig, {
          percent: 0,
          status: 'normal'
        })
      }
      if (val === 'Failed') {
        Object.assign(progressConfig, {
          percent: 100,
          status: 'exception'
        })
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
      :percent="percent"
      :status="status"
      :stroke-color="State[props.state]"
    />
  </div>
</template>

<style lang="scss" scoped></style>
