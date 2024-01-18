<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import { useServiceStore } from '@/store/service'
  import { ServiceVO } from '@/api/service/types.ts'
  import { MergedServiceVO } from '@/store/service/types.ts'
  import { QuestionCircleOutlined } from '@ant-design/icons-vue'
  import { useConfigStore } from '@/store/config'
  import { storeToRefs } from 'pinia'
  import { ServiceConfigVO, TypeConfigVO } from '@/api/config/types.ts'
  const route = useRoute()
  const serviceStore = useServiceStore()
  const configStore = useConfigStore()
  const { latestConfigs } = storeToRefs(configStore)

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

  const configs = ref([])

  watch(latestConfigs, (newVal) => {
    configs.value = newVal
      .filter((sc: ServiceConfigVO) => sc.serviceName === serviceName.value)
      .flatMap((sc: ServiceConfigVO) => sc.configs)
      .map((cd: TypeConfigVO) => ({
        typeName: cd.typeName,
        properties: cd.properties
      }))
  })

  const activeConfig = ref(null)

  watch(configs, (newVal) => {
    console.log(newVal)
    activeConfig.value = newVal.length > 0 ? newVal[0].typeName : null
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
      <div>this is {{ currentService.displayName }} page</div>
      <div>this is {{ currentService.id }} page</div>
    </a-tab-pane>

    <a-tab-pane key="config" tab="Config" force-render>
      <a-collapse v-model:active-key="activeConfig" ghost accordion>
        <a-collapse-panel
          v-for="config in configs"
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
