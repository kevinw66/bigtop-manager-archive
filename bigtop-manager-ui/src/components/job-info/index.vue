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
  import {
    CheckCircleTwoTone,
    MinusCircleTwoTone,
    CloseCircleTwoTone,
    LoadingOutlined,
    SettingOutlined
  } from '@ant-design/icons-vue'
  import { onBeforeUnmount, onMounted, ref, watch, computed } from 'vue'
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'
  import ClipboardJS from 'clipboard'
  import { useJobStore } from '@/store/job'
  import { message } from 'ant-design-vue'
  import { JobVO, StageVO, TaskVO } from '@/api/job/types.ts'
  import { getLogs } from '@/api/sse/index'
  import { AxiosProgressEvent } from 'axios'
  const columns = [
    {
      title: 'common.name',
      dataIndex: 'name',
      align: 'center'
    },
    {
      title: 'common.status',
      dataIndex: 'state',
      align: 'center'
    },
    {
      title: 'common.create_time',
      dataIndex: 'createTime',
      align: 'center'
    },
    {
      title: 'common.update_time',
      dataIndex: 'updateTime',
      align: 'center'
    }
  ]

  const clusterStore = useClusterStore()
  const jobStore = useJobStore()
  const { jobs, processJobNum } = storeToRefs(jobStore)
  const { clusterId } = storeToRefs(clusterStore)

  const jobWindowOpened = ref(false)
  const stages = ref<StageVO[]>([])
  const tasks = ref<TaskVO[]>([])
  const breadcrumbs = ref<string[]>(['Job Info'])
  const currTaskInfo = ref<TaskVO>()
  const logTextOrigin = ref<string>('')
  const currPage = ref<string[]>([
    'isJobTable',
    'isStageTable',
    'isTaskTable',
    'isTaskLogs'
  ])

  const logText = computed(() => {
    return logTextOrigin.value
      .split('\n\n')
      .map((s) => {
        return s.substring(5)
      })
      .join('\n')
  })

  const getCurrPage = computed(() => {
    return currPage.value[breadcrumbs.value.length - 1]
  })

  watch(jobWindowOpened, (val) => {
    if (!val) {
      breadcrumbs.value = ['Job Info']
    }
  })

  const jobOpen = () => {
    jobWindowOpened.value = true
  }

  const getLogsInfo = (id: number) => {
    getLogs(clusterId.value, id, ({ event }: AxiosProgressEvent) => {
      logTextOrigin.value = event.target.responseText
      const logsBox = document.querySelector('.logs_info') as HTMLElement
      if (logsBox) {
        ;(function smoothscroll() {
          const currentScroll = logsBox?.scrollTop
          const clientHeight = logsBox?.offsetHeight
          const scrollHeight = logsBox?.scrollHeight
          if (scrollHeight - 10 > currentScroll + clientHeight) {
            window.requestAnimationFrame(smoothscroll)
            logsBox.scrollTo(
              0,
              currentScroll + (scrollHeight - currentScroll - clientHeight) / 2
            )
          }
        })()
      }
    })
  }

  const copyLogTextContent = () => {
    const clipboard = new ClipboardJS('.copyBtn', {
      text: () => logText.value
    })
    if (!logText.value) {
      console.error('No text to copy')
      return
    }
    clipboard.on('success', () => {
      message.success('copy success!')
      clipboard.destroy()
    })
    clipboard.on('error', () => {
      message.success('Copy failed!')
      clipboard.destroy()
    })
  }

  const clickTask = (record: TaskVO) => {
    breadcrumbs.value.push(record.name)
    currTaskInfo.value = record
    getLogsInfo(record.id)
  }

  const clickJob = (record: JobVO) => {
    stages.value = record.stages
    breadcrumbs.value.push(record.name)
  }

  const clickStage = (record: StageVO) => {
    tasks.value = record.tasks
    breadcrumbs.value.push(record.name)
  }

  const clickBreadCrumbs = (idx: number) => {
    const len = breadcrumbs.value.length
    breadcrumbs.value.splice(idx + 1, len)
  }

  onMounted(async () => {
    jobStore.resumeIntervalFn()
  })

  onBeforeUnmount(() => {
    jobStore.pauseIntervalFn()
  })
</script>

<template>
  <div class="icon">
    <a-badge size="small" color="blue" :count="processJobNum" @click="jobOpen">
      <setting-outlined />
    </a-badge>
  </div>

  <a-modal v-model:open="jobWindowOpened" width="95%">
    <template #footer>
      <a-button key="back" type="primary" @click="jobWindowOpened = false">
        {{ $t('common.cancel') }}
      </a-button>
    </template>

    <div class="breadcrumb">
      <a-breadcrumb>
        <a-breadcrumb-item
          v-for="(item, idx) in breadcrumbs"
          :key="idx"
          @click="clickBreadCrumbs(idx)"
        >
          <a href="#">{{ item }}</a>
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <a-table
      v-if="getCurrPage == 'isJobTable'"
      destroy-on-close
      :data-source="jobs"
      :columns="columns"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <a @click="clickJob(record)">
            {{ text }}
          </a>
        </template>
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="text === 'Successful'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'Failed'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'Canceled'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
    <a-table
      v-else-if="getCurrPage == 'isStageTable'"
      :data-source="stages"
      :columns="columns"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <a @click="clickStage(record)">
            {{ text }}
          </a>
        </template>
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="text === 'Successful'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'Failed'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'Canceled'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
    <a-table
      v-else-if="getCurrPage == 'isTaskTable'"
      :data-source="tasks"
      :columns="columns"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <a @click="clickTask(record)">
            {{ text }}
          </a>
        </template>
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="text === 'Successful'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'Failed'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'Canceled'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
    <template v-else-if="getCurrPage == 'isTaskLogs'">
      <div class="logs">
        <div class="logs_header">
          <span>Task Logs</span>
          <div class="logs_header-ops">
            <a-button
              class="copyBtn"
              size="small"
              type="primary"
              @click="copyLogTextContent"
            >
              copy
            </a-button>
          </div>
        </div>
        <div class="logs_info">
          <pre id="logs">{{ logText }}</pre>
        </div>
      </div>
    </template>
  </a-modal>
</template>

<style lang="scss" scoped>
  .breadcrumb {
    :deep(.ant-breadcrumb) {
      margin-bottom: 16px !important;
    }
  }
  .icon {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
    cursor: pointer;
    border-radius: 50%;
    height: 36px;
    width: 36px;

    &:hover {
      background-color: var(--hover-color);
    }
  }
  .logs {
    height: 50vh;
    display: flex;
    flex-direction: column;
    &_header {
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 10px 0;
      display: flex;
      justify-content: space-between;
    }
    &_info {
      height: 100%;
      overflow: auto;
      background-color: #f5f5f5;
      border-radius: 4px;
      position: relative;
      pre {
        margin: 0;
        padding: 16px 14px;
        box-sizing: border-box;
        color: #444;
        border-color: #eee;
        line-height: 16px;
      }
    }
  }
</style>
