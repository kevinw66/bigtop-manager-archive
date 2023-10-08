<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { useWebSocket } from '@vueuse/core'
  import { WS_DEFAULT_OPTIONS, WS_URL } from '@/utils/constant.ts'

  const { t } = useI18n()

  useWebSocket(WS_URL, WS_DEFAULT_OPTIONS)

  const installColumns = [
    {
      title: t('common.host'),
      dataIndex: 'host',
      align: 'center'
    },
    {
      title: t('common.progress'),
      dataIndex: 'progress',
      align: 'center'
    }
  ]

  const data1 = [
    {
      key: 'host-1',
      host: 'bigtop-manager-server',
      progress: 50
    },
    {
      key: 'host-2',
      host: 'bigtop-manager-agent-01',
      progress: 70
    },
    {
      key: 'host-3',
      host: 'bigtop-manager-agent-02',
      progress: 99
    },
    {
      key: 'host-4',
      host: 'bigtop-manager-agent-03',
      progress: 100
    }
  ]

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
      :data-source="data1"
    >
      <template #bodyCell="{ text, column }">
        <!--        <template v-if="column.dataIndex === 'progress'">-->
        <a-progress
          v-if="column.dataIndex === 'progress'"
          class="progress"
          :percent="text"
        />
        <!--        </template>-->
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
