<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { TableProps } from 'ant-design-vue'
  import { useServiceStore } from '@/store/service'
  import { MergedServiceVO } from '@/store/service/types.ts'
  import { onMounted } from 'vue'
  import { ServiceVO } from '@/api/service/types.ts'
  import _ from 'lodash'

  const serviceInfo = defineModel<any>('serviceInfo')
  const disableButton = defineModel<boolean>('disableButton')

  const serviceStore = useServiceStore()
  const { installedServices, mergedServices } = storeToRefs(serviceStore)

  const defaultSelected = serviceInfo.value.serviceCommands.map(
    (item: any) => item.serviceName
  )

  const installedServiceNames = installedServices.value.map(
    (item: ServiceVO) => item.serviceName
  )

  const serviceColumns = [
    {
      title: 'service.service',
      dataIndex: 'displayName',
      align: 'center',
      width: 150
    },
    {
      title: 'common.version',
      dataIndex: 'serviceVersion',
      align: 'center',
      width: 150
    },
    {
      title: 'common.desc',
      dataIndex: 'serviceDesc',
      align: 'center'
    }
  ]

  const rowSelection: TableProps['rowSelection'] = {
    defaultSelectedRowKeys: defaultSelected,
    onChange: (v: (string | number)[]) => {
      // if difference is empty, keep installed services in serviceInfo
      // otherwise, add new service to serviceInfo
      const difference = _.difference(v, installedServiceNames)
      if (difference.length === 0) {
        _.remove(
          serviceInfo.value.serviceCommands,
          (item: any) => !installedServiceNames.includes(item.serviceName)
        )
      } else {
        const exists = serviceInfo.value.serviceCommands.map(
          (item: any) => item.serviceName
        )

        difference.map((item: string | number) => {
          if (!exists.includes(item)) {
            serviceInfo.value.serviceCommands.push({
              serviceName: item
            })
          }
        })
      }

      disableButton.value = _.isEqual(v, installedServiceNames)
    },
    getCheckboxProps: (record: MergedServiceVO) => ({
      disabled: record.installed
    })
  }

  onMounted(async () => {
    disableButton.value = _.isEqual(defaultSelected, installedServiceNames)
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
      :scroll="{ y: 400 }"
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
