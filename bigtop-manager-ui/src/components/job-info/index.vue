<script setup lang="ts">
  import {
    CheckCircleTwoTone,
    MinusCircleTwoTone,
    CloseCircleTwoTone,
    LoadingOutlined,
    SettingOutlined
  } from '@ant-design/icons-vue'
  import { onBeforeUnmount, onMounted, ref } from 'vue'
  import { storeToRefs } from 'pinia'
  import { useJobStore } from '@/store/job'
  import { JobVO, StageVO, TaskVO } from '@/api/job/types.ts'
  import { message } from 'ant-design-vue'

  const jobStore = useJobStore()
  const { jobs, processJobNum } = storeToRefs(jobStore)

  const jobWindowOpened = ref(false)
  const isStageTable = ref(false)
  const isJobTable = ref(false)
  const isTaskTable = ref(false)
  const stages = ref<StageVO[]>([])
  const tasks = ref<TaskVO[]>([])
  const title = 'Job Info'
  const currentJobTitle = ref({})
  const currentStageTitle = ref({})

  const jobOpen = () => {
    jobWindowOpened.value = true
    navJob()
  }

  const clickJob = (record: JobVO) => {
    stages.value = record.stages
    navStage()
    currentJobTitle.value = record.name
  }

  const clickStage = (record: StageVO) => {
    tasks.value = record.tasks
    navTask()
    currentStageTitle.value = record.name
  }

  const navJob = () => {
    isJobTable.value = true
    isStageTable.value = false
    isTaskTable.value = false
  }

  const navStage = () => {
    isJobTable.value = false
    isStageTable.value = true
    isTaskTable.value = false
  }

  const navTask = () => {
    isJobTable.value = false
    isStageTable.value = false
    isTaskTable.value = true
  }

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
    <a-breadcrumb>
      <a-breadcrumb-item @click="navJob"
        ><a href="#">{{ title }}</a></a-breadcrumb-item
      >
      <a-breadcrumb-item v-if="!isJobTable" @click="navStage"
        ><a href="#">{{ currentJobTitle }}</a></a-breadcrumb-item
      >
      <a-breadcrumb-item v-if="isTaskTable" @click="navTask"
        ><a href="#">{{ currentStageTitle }}</a></a-breadcrumb-item
      >
    </a-breadcrumb>
    <a-table
      v-if="isJobTable"
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
            v-if="text === 'SUCCESSFUL'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'FAILED'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'CANCELED'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
    <a-table v-else-if="isStageTable" :data-source="stages" :columns="columns">
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
            v-if="text === 'SUCCESSFUL'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'FAILED'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'CANCELED'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
    <a-table v-else-if="isTaskTable" :data-source="tasks" :columns="columns">
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'name'">
          <a @click="message.info(`Test message [${record.name}`)">
            {{ text }}
          </a>
        </template>
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="text === 'SUCCESSFUL'"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone
            v-else-if="text === 'FAILED'"
            two-tone-color="red"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'CANCELED'"
            two-tone-color="orange"
          />
          <LoadingOutlined v-else />
        </template>
      </template>
    </a-table>
  </a-modal>
</template>

<style lang="scss" scoped>
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
</style>
