<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { TableProps } from 'ant-design-vue'
  import { useServiceStore } from '@/store/service'
  import { MergedServiceVO } from '@/store/service/types.ts'
  import { onMounted } from 'vue'

  const serviceInfo = defineModel<any>('serviceInfo')
  const disableButton = defineModel<boolean>('disableButton')

  const serviceStore = useServiceStore()
  const { mergedServices } = storeToRefs(serviceStore)

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

  const disableButtonIfNoService = () => {
    disableButton.value = serviceInfo.value.serviceNames.length === 0
  }

  const rowSelection: TableProps['rowSelection'] = {
    defaultSelectedRowKeys: serviceInfo.value.serviceNames,
    onChange: (v: (string | number)[]) => {
      serviceInfo.value.serviceNames = v
      disableButtonIfNoService()
    },
    getCheckboxProps: (record: MergedServiceVO) => ({
      disabled: record.installed,
      name: record.serviceName
    })
  }

  onMounted(async () => {
    disableButtonIfNoService()
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
