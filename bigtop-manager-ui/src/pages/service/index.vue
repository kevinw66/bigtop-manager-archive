<script setup lang="ts">
  import { computed, onMounted, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import { useServiceStore } from '@/store/service'
  import { ServiceVO } from '@/api/service/types.ts'
  import type { SelectProps } from 'ant-design-vue'
  import { MergedServiceVO } from '@/store/service/types.ts'
  import {
    CheckCircleTwoTone,
    CloseCircleTwoTone,
    MinusCircleTwoTone,
    QuestionCircleOutlined
  } from '@ant-design/icons-vue'
  import { useConfigStore } from '@/store/config'
  import { storeToRefs } from 'pinia'
  import { ServiceConfigVO, TypeConfigVO } from '@/api/config/types.ts'
  import { useComponentStore } from '@/store/component'
  import { HostComponentVO } from '@/api/component/types.ts'

  const route = useRoute()
  const serviceStore = useServiceStore()
  const configStore = useConfigStore()
  const { allConfigs } = storeToRefs(configStore)
  const componentStore = useComponentStore()
  const { hostComponents } = storeToRefs(componentStore)

  const serviceName = ref<string>(route.params.serviceName as string)

  const currentService = computed(() => {
    let currentService: ServiceVO
    serviceStore.mergedServices.forEach((mergedService: MergedServiceVO) => {
      if (mergedService.serviceName === serviceName.value) {
        currentService = mergedService
      }
    })
    return currentService
  })

  // summary model start
  let currentHostComponent = ref([])
  let masterHostComponent = ref([])
  watch(hostComponents, (newVal) => {
    currentHostComponent.value = newVal.filter(
      (hc: HostComponentVO) => hc.serviceName === serviceName.value
    )

    masterHostComponent.value = currentHostComponent.value
      .filter((hc: HostComponentVO) => hc.category === 'master')
      .map((hc: HostComponentVO) => ({
        title: hc.displayName,
        hostname: hc.hostname,
        category: hc.category,
        state: hc.state
      }))
  })
  // summary model end

  // config model start
  const serviceConfigDesc = ref<SelectProps['options']>([])
  let value = ref()
  let currentConfigs = ref([])
  let currentConfigVersion = ref<number>()
  let initConfigVersion = ref<number>()

  watch(allConfigs, (newVal) => {
    serviceConfigDesc.value = newVal
      .filter((sc: ServiceConfigVO) => sc.serviceName === serviceName.value)
      .map((sc: ServiceConfigVO) => ({
        value: sc.version,
        label: `Version: ${sc.version}`,
        title: `${sc.configDesc}`
      }))
    initConfigVersion
    loadCurrentConfigs()
  })

  const loadCurrentConfigs = () => {
    if (!currentConfigVersion.value) {
      currentConfigVersion.value = serviceConfigDesc.value[0].value
    }

    currentConfigs.value = allConfigs.value
      .filter(
        (sc: ServiceConfigVO) =>
          (sc.serviceName === serviceName.value) &
          (currentConfigVersion.value === sc.version)
      )
      .flatMap((sc: ServiceConfigVO) => sc.configs)
      .map((cd: TypeConfigVO) => ({
        typeName: cd.typeName,
        properties: cd.properties
      }))
  }

  const handleChange: SelectProps['onChange'] = (value) => {
    currentConfigVersion.value = value.key
    loadCurrentConfigs()
  }

  const activeConfig = ref(null)

  watch(currentConfigs, (newVal) => {
    activeConfig.value = newVal.length > 0 ? newVal[0].typeName : null
  })

  // config model end

  onMounted(async () => {
    await configStore.loadLatestConfigs()
    await configStore.loadAllConfigs()
    value.value = serviceConfigDesc.value[0]
    currentConfigVersion.value = initConfigVersion.value =
      serviceConfigDesc.value[0].value

    await componentStore.loadHostComponents()
  })

  watch(
    () => route.params,
    (params) => {
      serviceName.value = params.serviceName as string
    }
  )
</script>

<template>
  <a-tabs>
    <a-tab-pane key="summary" tab="Summary">
      <template v-if="masterHostComponent.length > 0">
        <div class="summary">
          <template v-for="(item, index) in masterHostComponent" :key="index">
            <a-card :title="item.title" class="card" :bordered="false">
              <p>{{ item.hostname }}</p>
              <p>{{ item.state }}</p>
              <CheckCircleTwoTone
                v-if="item.state === 'STARTED'"
                two-tone-color="#52c41a"
              />
              <MinusCircleTwoTone
                v-else-if="item.state === 'MAINTAINED'"
                two-tone-color="orange"
              />
              <CloseCircleTwoTone v-else two-tone-color="red" />
            </a-card>
          </template>
        </div>
      </template>
    </a-tab-pane>

    <a-tab-pane key="config" tab="Config" force-render>
      <a-space>
        <a-select
          v-model:value="value"
          label-in-value
          :options="serviceConfigDesc"
          @change="handleChange"
        ></a-select>
        <template v-if="initConfigVersion != currentConfigVersion">
          <a-button type="primary">Not Current Config</a-button></template
        >
      </a-space>

      <a-divider />
      <a-collapse v-model:active-key="activeConfig" ghost accordion>
        <a-collapse-panel
          v-for="config in currentConfigs"
          :key="config.typeName"
          class="panel"
          :header="config.typeName"
        >
          <div
            v-for="property in config.properties"
            :key="property.name"
            class="config-item"
          >
            <div class="config-item-key">
              {{ property.displayName ?? property.name }}
            </div>
            <div class="config-item-value">
              <a-input v-model:value="property.value" />
            </div>
            <a-tooltip>
              <template #title>
                {{ property.desc }}
              </template>
              <question-circle-outlined class="config-item-desc" />
            </a-tooltip>
          </div>
        </a-collapse-panel>
      </a-collapse>
    </a-tab-pane>
  </a-tabs>
</template>

<style scoped lang="scss">
  .summary {
    display: flex;
    flex-direction: row;
    justify-content: space-around;

    .card {
      width: 30%;
    }
  }

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

    .content {
      width: 100%;
      height: 100%;
      margin-top: 1rem;
      overflow-y: auto;

      .panel {
        text-align: start;

        .config-item {
          display: flex;
          align-items: center;
          justify-content: start;
          margin-bottom: 1rem;

          .config-item-key {
            width: 20%;
            text-align: start;
            margin-left: 2rem;
          }

          .config-item-value {
            width: 60%;
          }

          .config-item-desc {
            cursor: pointer;
            margin-left: 1rem;
          }
        }
      }
    }
  }
</style>
