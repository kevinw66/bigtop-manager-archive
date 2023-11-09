<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue'
  import { message } from 'ant-design-vue'
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'
  import { getHosts } from '@/api/hosts/index.ts'
  import { HostVO } from '@/api/hosts/types.ts'
  import {
    CaretRightFilled,
    CheckCircleTwoTone,
    CloseCircleTwoTone,
    MinusCircleTwoTone,
    StopFilled
  } from '@ant-design/icons-vue'
  import { useIntervalFn } from '@vueuse/core'
  import { MONITOR_SCHEDULE_INTERVAL } from '@/utils/constant.ts'

  const clusterStore = useClusterStore()
  const { clusterId } = storeToRefs(clusterStore)
  watch(clusterId, async () => {
    await refreshHosts()
  })

  const totalRecord = ref<number>(0)
  const hostData = ref<HostVO[]>([])
  const selectedRowKeys = ref<string[]>([])
  const loading = ref<boolean>(true)

  const hostColumns = [
    {
      title: 'hosts.hostname',
      dataIndex: 'hostname',
      align: 'center'
    },
    {
      title: 'hosts.cluster_name',
      dataIndex: 'clusterName',
      align: 'center'
    },
    {
      title: 'common.os',
      dataIndex: 'os',
      align: 'center'
    },
    {
      title: 'common.arch',
      dataIndex: 'arch',
      align: 'center'
    },
    {
      title: 'hosts.ipv4',
      dataIndex: 'ipv4',
      align: 'center'
    },
    {
      title: 'hosts.cores',
      dataIndex: 'availableProcessors',
      align: 'center'
    },
    {
      title: 'hosts.ram',
      dataIndex: 'totalMemorySize',
      align: 'center'
    },
    {
      title: 'hosts.disk',
      dataIndex: 'disk',
      align: 'center'
    },
    {
      title: 'common.status',
      dataIndex: 'state',
      align: 'center'
    }
  ]

  const displayKeys = (selectedRowKeys: string[]) => {
    message.info('This is a normal message' + selectedRowKeys)
  }

  const refreshHosts = async () => {
    if (clusterId.value !== 0) {
      const res = await getHosts(clusterId.value)
      totalRecord.value = res.total
      hostData.value = res.content
      loading.value = false
    }
  }

  onMounted(async () => {
    useIntervalFn(
      async () => {
        await refreshHosts()
      },
      MONITOR_SCHEDULE_INTERVAL,
      { immediateCallback: true }
    )
  })
</script>

<template>
  <div>
    <a-page-header class="host-page-header" :title="$t('common.host')">
      <template #extra>
        <span class="host-selected-span">
          <template v-if="selectedRowKeys.length > 0">
            {{ $t('hosts.host_selected', [selectedRowKeys.length]) }}
          </template>
        </span>

        <a-button type="primary" @click="displayKeys(selectedRowKeys)">
          {{ $t('hosts.add') }}
        </a-button>

        <a-dropdown :trigger="['click']">
          <template #overlay>
            <a-menu>
              <a-menu-item @click="displayKeys(selectedRowKeys)">
                <CaretRightFilled />
                {{ $t('hosts.host_restart') }}
              </a-menu-item>
              <a-menu-item @click="displayKeys(selectedRowKeys)">
                <StopFilled />
                {{ $t('hosts.host_stop') }}
              </a-menu-item>
            </a-menu>
          </template>
          <a-button type="primary" :disabled="selectedRowKeys.length === 0">
            {{ $t('common.action') }}
          </a-button>
        </a-dropdown>
      </template>
    </a-page-header>
    <br />

    <a-table
      :row-key="hostColumns[0].dataIndex"
      :columns="hostColumns"
      :data-source="hostData"
      :loading="loading"
      :row-selection="{
        selectedRowKeys,
        onChange: (value: string[]) => (selectedRowKeys = value)
      }"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="text === 'INSTALLED'"
            two-tone-color="#52c41a"
          />
          <MinusCircleTwoTone
            v-else-if="text === 'MAINTAINED'"
            two-tone-color="orange"
          />
          <CloseCircleTwoTone v-else two-tone-color="red" />
        </template>
        <template v-if="column.dataIndex === 'hostname'">
          <router-link to="/dashboard"> {{ text }} </router-link>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped lang="scss">
  .host-page-header {
    border: 1px solid rgb(235, 237, 240);

    .host-selected-span {
      margin-left: 8px;
    }
  }
</style>
