<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { useServiceStore } from '@/store/service'

  const serviceStore = useServiceStore()
  const { installedServices, loadingServices } = storeToRefs(serviceStore)

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
      :data-source="installedServices"
      :columns="serviceColumns"
      :loading="loadingServices"
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
