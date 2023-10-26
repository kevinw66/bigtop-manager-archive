<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { get } from '@/api/job'
  import { SCHEDULE_INTERVAL } from '@/utils/constant.ts'
  import { useIntervalFn } from '@vueuse/core'
  import { onBeforeMount, onBeforeUnmount, reactive, ref } from 'vue'
  import { onUnmounted } from 'vue'

  const clusterInfo = defineModel<any>('clusterInfo')
  const disableButton = defineModel<boolean>('disableButton')

  const { t } = useI18n()
  const installData = reactive([])
  const loading = ref(true)
  const jobState = ref('')

  const initData = async () => {
    const res = await get(clusterInfo.value.jobId)
    console.log(res)
    jobState.value = res.state

    if (jobState.value !== 'PENDING') {
      disableButton.value = false
      clusterInfo.value.success = jobState.value === 'SUCCESSFUL'
    }

    const arr: any[] = []
    res.stages
      .sort((a, b) => a.stageOrder - b.stageOrder)
      .forEach((stage) => {
        const data = {
          key: stage.id,
          stage: stage.name,
          progress: 0,
          status: '',
          color: ''
        }

        if (stage.state === 'PENDING') {
          data.progress = 0
          data.status = 'normal'
          data.color = '#1677ff'
        } else if (stage.state === 'PROCESSING') {
          data.progress = Math.round(
            (stage.tasks.filter((task) => task.state === 'SUCCESSFUL').length /
              stage.tasks.length) *
              100
          )
          data.status = 'active'
          data.color = '#1677ff'
        } else if (stage.state === 'SUCCESSFUL') {
          data.progress = 100
          data.status = 'success'
          data.color = '#52c41a'
        } else if (stage.state === 'CANCELED') {
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
        if (jobState.value !== 'PENDING' && jobState.value !== 'PROCESSING') {
          pause()
        }
      },
      SCHEDULE_INTERVAL,
      { immediate: true }
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
