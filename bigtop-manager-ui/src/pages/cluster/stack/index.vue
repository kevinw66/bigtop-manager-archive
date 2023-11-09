<script setup lang="ts">
  import { onMounted, reactive, ref, watch } from 'vue'
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'
  import { CheckCircleTwoTone, CloseCircleTwoTone } from '@ant-design/icons-vue'
  import { useStackStore } from '@/store/stack'
  import ClusterCreate from '@/components/cluster-create/index.vue'
  import { getService } from '@/api/service'
  import { ServiceVO } from '@/api/service/types.ts'
  import { StackServiceVO } from '@/api/stack/types.ts'
  import { useIntervalFn } from '@vueuse/core/index'
  import { MONITOR_SCHEDULE_INTERVAL } from '@/utils/constant.ts'

  const stackStore = useStackStore()
  const { stackServices } = storeToRefs(stackStore)
  const clusterStore = useClusterStore()
  const { clusterId, selectedCluster } = storeToRefs(clusterStore)
  watch(clusterId, async () => {
    await refreshService()
  })

  const selectedRowKeys = ref<string[]>([])
  const loading = ref<boolean>(true)
  const nameServiceVOs = reactive<Record<string, ServiceVO>>({})

  const createWindowOpened = ref(false)

  const serviceColumns = [
    {
      title: 'common.name',
      dataIndex: 'serviceName',
      align: 'center'
    },
    {
      title: 'common.version',
      dataIndex: 'serviceVersion',
      align: 'center'
    },
    {
      title: 'common.status',
      dataIndex: 'state',
      align: 'center'
    },
    {
      title: 'common.desc',
      dataIndex: 'serviceDesc',
      align: 'center'
    }
  ]

  const refreshService = async () => {
    if (clusterId.value !== 0) {
      const res = await getService(clusterId.value)
      res.forEach((serviceVO) => {
        nameServiceVOs[serviceVO.serviceName] = serviceVO
      })
      loading.value = false
    }
  }

  onMounted(async () => {
    useIntervalFn(
      async () => {
        await refreshService()
      },
      MONITOR_SCHEDULE_INTERVAL,
      { immediateCallback: true }
    )
  })
</script>

<template>
  <div>
    <a-page-header class="host-page-header" :title="$t('common.service')">
      <template #extra>
        <span class="host-selected-span">
          <template v-if="selectedRowKeys.length > 0">
            {{ $t('service.service_selected', [selectedRowKeys.length]) }}
          </template>
        </span>

        <a-button
          type="primary"
          :disabled="selectedRowKeys.length === 0"
          @click="createWindowOpened = true"
        >
          {{ $t('service.add_service') }}
        </a-button>
      </template>
    </a-page-header>
    <br />

    <a-table
      :row-key="serviceColumns[0].dataIndex"
      :columns="serviceColumns"
      :data-source="
        stackServices[
          selectedCluster?.stackName + '-' + selectedCluster?.stackVersion
        ]
      "
      :row-selection="{
        selectedRowKeys,
        onChange: (value: string[]) => (selectedRowKeys = value),
        getCheckboxProps: (record: StackServiceVO) => ({
          disabled: nameServiceVOs[record.serviceName] !== undefined
        })
      }"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'state'">
          <CheckCircleTwoTone
            v-if="nameServiceVOs[record.serviceName] !== undefined"
            two-tone-color="#52c41a"
          />
          <CloseCircleTwoTone v-else two-tone-color="red" />
        </template>
        <template
          v-if="
            column.dataIndex === 'serviceName' &&
            nameServiceVOs[record.serviceName] !== undefined
          "
        >
          <router-link :to="'/services/' + record.serviceName">
            {{ text }}
          </router-link>
        </template>
      </template>
    </a-table>
  </div>

  <cluster-create v-model:open="createWindowOpened" />
</template>

<style scoped lang="scss">
  .host-page-header {
    border: 1px solid rgb(235, 237, 240);

    .host-selected-span {
      margin-left: 8px;
    }
  }
</style>
