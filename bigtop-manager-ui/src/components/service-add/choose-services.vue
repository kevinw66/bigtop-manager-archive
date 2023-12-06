<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { TableProps } from 'ant-design-vue'
  import { useServiceStore } from '@/store/service'
  import { MergedServiceVO } from '@/store/service/types.ts'
  import { computed, onMounted } from 'vue'
  import { ServiceVO } from '@/api/service/types.ts'
  import _ from 'lodash'

  const serviceInfo = defineModel<any>('serviceInfo')
  const disableButton = defineModel<boolean>('disableButton')

  const serviceStore = useServiceStore()
  const { installedServices, mergedServices } = storeToRefs(serviceStore)
  const installedServiceNames = computed(() => {
    return installedServices.value.map((item: ServiceVO) => item.serviceName)
  })

  const serviceColumns = [
    {
      title: 'service.service',
      dataIndex: 'displayName',
      align: 'center'
    },
    {
      title: 'common.version',
      dataIndex: 'serviceVersion',
      align: 'center'
    },
    {
      title: 'common.desc',
      dataIndex: 'serviceDesc',
      align: 'center'
    }
  ]

  const rowSelection: TableProps['rowSelection'] = {
    defaultSelectedRowKeys: [
      ...serviceInfo.value.serviceNames,
      ...installedServiceNames.value
    ],
    onChange: (v: (string | number)[]) => {
      serviceInfo.value.serviceNames = _.difference(
        v,
        installedServiceNames.value
      )
      disableButton.value = serviceInfo.value.serviceNames.length === 0
    },
    getCheckboxProps: (record: MergedServiceVO) => ({
      disabled: record.installed
    })
  }

  onMounted(async () => {
    disableButton.value = serviceInfo.value.serviceNames.length === 0
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
    <div class="title">{{ $t('service.choose_services') }}</div>
    <a-table
      row-key="serviceName"
      :columns="serviceColumns"
      :data-source="mergedServices"
      :row-selection="rowSelection"
      :pagination="false"
    >
      <template #headerCell="{ column }">
        <span>{{ $t(column.title) }}</span>
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
    }
  }
</style>
