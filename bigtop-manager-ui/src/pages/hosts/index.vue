<script setup lang="ts">
  import { list } from '@/api/hosts/index.ts'
  import { HostVO } from '@/api/hosts/types.ts'
  import { onMounted, reactive, computed } from 'vue'
  import { message } from 'ant-design-vue'
  import {
    CloseCircleTwoTone,
    CheckCircleTwoTone,
    MinusCircleTwoTone,
    CaretRightFilled,
    StopFilled
  } from '@ant-design/icons-vue'

  const hostData = reactive([])

  const getHostData = async () => {
    const hostVOList: HostVO[] = await list()
    return hostVOList
  }

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
      title: 'hosts.status',
      dataIndex: 'status',
      align: 'center'
    }
  ]

  onMounted(async () => {
    Object.assign(hostData, await getHostData())
  })

  const state = reactive<{
    selectedRowKeys: string[]
    loading: boolean
  }>({
    selectedRowKeys: [], // Check here to configure the default column
    loading: false
  })

  const hasSelected = computed(() => state.selectedRowKeys.length > 0)

  const onSelectChange = (selectedRowKeys: string[]) => {
    console.log('selectedRowKeys changed: ', selectedRowKeys)
    state.selectedRowKeys = selectedRowKeys
  }

  const displayKeys = (selectedRowKeys: string[]) => {
    console.log('selectedRowKeys changed: ', selectedRowKeys)
    message.info('This is a normal message' + selectedRowKeys)
  }
</script>

<template>
  <div>
    <a-page-header class="host-page-header" :title="$t('common.host')">
      <template #extra>
        <span class="host-selected-span">
          <template v-if="hasSelected">
            {{ $t('hosts.host_selected', [state.selectedRowKeys.length]) }}
          </template>
        </span>

        <a-button
          type="primary"
          :loading="state.loading"
          @click="displayKeys(state.selectedRowKeys)"
        >
          {{ $t('hosts.add') }}
        </a-button>

        <a-dropdown :trigger="['click']">
          <template #overlay>
            <a-menu>
              <a-menu-item @click="displayKeys(state.selectedRowKeys)">
                <CaretRightFilled />
                {{ $t('hosts.host_restart') }}
              </a-menu-item>
              <a-menu-item @click="displayKeys(state.selectedRowKeys)">
                <StopFilled />
                {{ $t('hosts.host_stop') }}
              </a-menu-item>
            </a-menu>
          </template>
          <a-button
            type="primary"
            :disabled="!hasSelected"
            :loading="state.loading"
          >
            {{ $t('common.action') }}
          </a-button>
        </a-dropdown>
      </template>
    </a-page-header>
    <br />

    <a-table
      row-key="hostname"
      :columns="hostColumns"
      :data-source="hostData"
      :row-selection="{
        selectedRowKeys: state.selectedRowKeys,
        onChange: onSelectChange
      }"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'status'">
          <a>
            <CheckCircleTwoTone
              v-if="record.status === '0'"
              two-tone-color="#52c41a"
            />
            <MinusCircleTwoTone
              v-else-if="record.status === '1'"
              two-tone-color="orange"
            />
            <CloseCircleTwoTone v-else two-tone-color="red" />
          </a>
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
