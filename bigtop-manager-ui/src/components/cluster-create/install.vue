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

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { getJob } from '@/api/job'
  import { JOB_SCHEDULE_INTERVAL } from '@/utils/constant.ts'
  import { useIntervalFn } from '@vueuse/core'
  import { onBeforeMount, onBeforeUnmount, reactive, ref } from 'vue'
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'

  const clusterInfo = defineModel<any>('clusterInfo')
  const disableButton = defineModel<boolean>('disableButton')

  const clusterStore = useClusterStore()
  const { clusterId } = storeToRefs(clusterStore)

  const { t } = useI18n()
  const installData = reactive([])
  const loading = ref(true)
  const jobState = ref('')

  const initData = async () => {
    const res = await getJob(clusterInfo.value.jobId, clusterId.value)
    console.log(res)
    jobState.value = res.state

    if (jobState.value !== 'Pending' && jobState.value !== 'Processing') {
      disableButton.value = false
      clusterInfo.value.success = jobState.value === 'Successful'
    }

    const arr: any[] = []
    res.stages
      .sort((a, b) => a.order - b.order)
      .forEach((stage) => {
        const data = {
          key: stage.id,
          stage: stage.name,
          progress: 0,
          status: '',
          color: ''
        }

        if (stage.state === 'Pending') {
          data.progress = 0
          data.status = 'normal'
          data.color = '#1677ff'
        } else if (stage.state === 'Processing') {
          data.progress = Math.round(
            ((stage.tasks.filter((task) => task.state === 'Successful').length +
              1) /
              stage.tasks.length) *
              100
          )
          data.status = 'active'
          data.color = '#1677ff'
        } else if (stage.state === 'Successful') {
          data.progress = 100
          data.status = 'success'
          data.color = '#52c41a'
        } else if (stage.state === 'Canceled') {
          data.progress = 0
          data.status = 'normal'
          data.color = '#8c908b'
        } else {
          data.progress = 100
          data.status = 'exception'
          data.color = '#ff4d4f'
        }

        arr.push(data)
      })

    return arr
  }

  const installColumns = [
    {
      title: t('common.stage'),
      dataIndex: 'stage',
      align: 'center'
    },
    {
      title: t('common.progress'),
      dataIndex: 'progress',
      align: 'center'
    }
  ]

  onBeforeMount(async () => {
    disableButton.value = true
    const { pause } = useIntervalFn(
      async () => {
        Object.assign(installData, await initData())
        loading.value = false
        if (jobState.value !== 'Pending' && jobState.value !== 'Processing') {
          pause()
        }
      },
      JOB_SCHEDULE_INTERVAL,
      { immediateCallback: true }
    )
  })

  onBeforeUnmount(() => {
    disableButton.value = false
  })

  const onNextStep = async () => {
    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('common.install') }}</div>
    <a-table
      :pagination="false"
      :scroll="{ y: 400 }"
      :columns="installColumns"
      :data-source="installData"
      :loading="loading"
    >
      <template #bodyCell="{ record, column }">
        <a-progress
          v-if="column.dataIndex === 'progress'"
          class="progress"
          :percent="record.progress"
          :status="record.status"
          :stroke-color="record.color"
        />
      </template>
    </a-table>
  </div>
</template>

<style scoped lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    align-content: center;
    height: 100%;

    .title {
      font-size: 1.5rem;
      line-height: 2rem;
      margin-bottom: 1rem;

      .progress {
        width: 80%;
      }
    }
  }
</style>
