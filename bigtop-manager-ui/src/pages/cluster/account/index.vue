<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { useClusterStore } from '@/store/cluster'
  import { onMounted, ref, watch } from 'vue'
  import { ServiceVO } from '@/api/service/types.ts'
  import { getService } from '@/api/service'
  import { useIntervalFn } from '@vueuse/core'
  import { MONITOR_SCHEDULE_INTERVAL } from '@/utils/constant.ts'

  const clusterStore = useClusterStore()
  const { clusterId } = storeToRefs(clusterStore)
  watch(clusterId, async () => {
    await refreshService()
  })
  const loading = ref<boolean>(true)
  const serviceData = ref<ServiceVO[]>([])

  const serviceColumns = [
    {
      title: 'service.service_name',
      dataIndex: 'displayName',
      align: 'center'
    },
    {
      title: 'service.service_user',
      dataIndex: 'serviceUser',
      align: 'center'
    },
    {
      title: 'service.service_group',
      dataIndex: 'serviceGroup',
      align: 'center'
    }
  ]

  const refreshService = async () => {
    if (clusterId.value !== 0) {
      serviceData.value = await getService(clusterId.value)
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
    <a-page-header
      class="account-page-header"
      :title="$t('service.service_account')"
    >
    </a-page-header>
    <br />
    <a-table
      :data-source="serviceData"
      :columns="serviceColumns"
      :loading="loading"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
      </template>
    </a-table>
  </div>
</template>

<style scoped lang="scss">
  .account-page-header {
    border: 1px solid rgb(235, 237, 240);
  }
</style>
