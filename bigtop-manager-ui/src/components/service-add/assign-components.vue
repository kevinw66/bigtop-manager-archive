<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { DEFAULT_PAGE_SIZE } from '@/utils/constant.ts'
  import { useHostStore } from '@/store/host'
  import { useComponentStore } from '@/store/component'
  import { storeToRefs } from 'pinia'
  import { useStackStore } from '@/store/stack'
  import { StackComponentVO } from '@/api/stack/types.ts'
  import _ from 'lodash'

  const serviceInfo = defineModel<any>('serviceInfo')

  const hostStore = useHostStore()
  const stackStore = useStackStore()
  const componentStore = useComponentStore()
  const { hosts, loading } = storeToRefs(hostStore)
  const { currentStack, stackServices, stackComponents } =
    storeToRefs(stackStore)
  const { hostComponents } = storeToRefs(componentStore)
  const pageSize = ref<number>(DEFAULT_PAGE_SIZE)

  const serviceNameToDisplayName = _.fromPairs(
    stackServices.value[currentStack.value.name].map((v) => [
      v.serviceName,
      v.displayName
    ])
  )

  const hostnames = _.sortBy(hosts.value.map((v) => v.hostname))
  const selectedComponents = _.mapValues(
    _.groupBy(hostComponents.value, 'componentName'),
    (g) => g.map((v) => v.hostname)
  )

  const columns = computed(() => {
    const services = Object.entries(
      _.omit(
        _.pick(stackComponents.value, serviceInfo.value.serviceNames),
        _.uniqBy(hostComponents.value, 'serviceName').map((v) => v.serviceName)
      )
    ).map(([serviceName, components]) => {
      return {
        title: serviceNameToDisplayName[serviceName],
        align: 'center',
        children: (components as StackComponentVO[]).map((component) => {
          return {
            title: component.displayName,
            dataIndex: component.componentName,
            align: 'center',
            width: 180
          }
        })
      }
    })

    const selectedServices = Object.entries(
      _.groupBy(_.uniqBy(hostComponents.value, 'componentName'), 'serviceName')
    ).map(([serviceName, components]) => {
      return {
        title: serviceNameToDisplayName[serviceName],
        align: 'center',
        children: components.map((component) => {
          return {
            title: component.displayName,
            dataIndex: component.componentName,
            align: 'center',
            width: 180
          }
        })
      }
    })

    return [
      {
        title: 'hosts.host',
        dataIndex: 'host',
        align: 'center',
        fixed: true,
        width: 200
      },
      ...services,
      ...selectedServices
    ]
  })

  const data = computed(() => hosts.value.map((v) => ({ host: v.hostname })))
  const pagination = computed(() => {
    return {
      pageSize: pageSize.value,
      showSizeChanger: true,
      onShowSizeChange: (_: number, size: number) => {
        pageSize.value = size
      }
    }
  })

  const checkComponent = (record: any, column: any) => {
    const host = record.host
    const componentName = column.dataIndex
    if (!serviceInfo.value.componentHosts[componentName]) {
      serviceInfo.value.componentHosts[componentName] = []
    }

    if (serviceInfo.value.componentHosts[componentName].includes(host)) {
      _.remove(
        serviceInfo.value.componentHosts[componentName],
        (v) => v === host
      )
      if (_.isEmpty(serviceInfo.value.componentHosts[componentName])) {
        delete serviceInfo.value.componentHosts[componentName]
      }
    } else {
      serviceInfo.value.componentHosts[componentName].push(host)
    }
  }

  const isComponentCheckDisabled = (column: any): boolean => {
    const componentName = column.dataIndex
    return (
      selectedComponents[componentName] &&
      !_.isEmpty(selectedComponents[componentName])
    )
  }

  const isComponentChecked = (record: any, column: any): boolean => {
    const host = record.host
    const componentName = column.dataIndex

    return (
      (serviceInfo.value.componentHosts[componentName] &&
        serviceInfo.value.componentHosts[componentName].includes(host)) ||
      (selectedComponents[componentName] &&
        selectedComponents[componentName].includes(host))
    )
  }

  const checkGroup = (column: any) => {
    const componentName = column.dataIndex
    if (serviceInfo.value.componentHosts[componentName]) {
      // Uncheck if there is elements
      delete serviceInfo.value.componentHosts[componentName]
    } else {
      // Check all
      serviceInfo.value.componentHosts[componentName] = [...hostnames]
    }
  }

  const isGroupIndeterminate = (column: any) => {
    const componentName = column.dataIndex
    return (
      serviceInfo.value.componentHosts[componentName] &&
      !_.isEqual(
        _.sortBy(serviceInfo.value.componentHosts[componentName]),
        hostnames
      )
    )
  }

  const isGroupCheckDisabled = (column: any): boolean => {
    const componentName = column.dataIndex
    return (
      selectedComponents[componentName] &&
      !_.isEmpty(selectedComponents[componentName])
    )
  }

  const isGroupChecked = (column: any) => {
    const componentName = column.dataIndex
    return (
      _.isEqual(
        _.sortBy(serviceInfo.value.componentHosts[componentName]),
        hostnames
      ) || _.isEqual(_.sortBy(selectedComponents[componentName]), hostnames)
    )
  }

  onMounted(() => {
    hostStore.refreshHosts()
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
    <div class="title">{{ $t('service.assign_components') }}</div>
    <a-table
      :loading="loading"
      :columns="columns"
      :data-source="data"
      :scroll="{ x: 'max-content', y: 330 }"
      :pagination="pagination"
    >
      <template #headerCell="{ column }">
        <span v-if="column.dataIndex === 'host'">{{ $t(column.title) }}</span>
        <template v-if="column.dataIndex !== 'host' && !column.children">
          <a-checkbox
            :indeterminate="isGroupIndeterminate(column)"
            :checked="isGroupChecked(column)"
            :disabled="isGroupCheckDisabled(column)"
            @click="checkGroup(column)"
          >
            {{ column.title }}
          </a-checkbox>
        </template>
      </template>

      <template #bodyCell="{ record, column }">
        <template v-if="column.dataIndex !== 'host'">
          <a-checkbox
            :checked="isComponentChecked(record, column)"
            :disabled="isComponentCheckDisabled(column)"
            @click="checkComponent(record, column)"
          />
        </template>
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
