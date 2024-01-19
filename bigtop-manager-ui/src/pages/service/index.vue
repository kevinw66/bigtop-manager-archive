<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import type { SelectProps, MenuProps } from 'ant-design-vue'
  import {
    CheckCircleTwoTone,
    CloseCircleTwoTone,
    MinusCircleTwoTone,
    QuestionCircleOutlined,
    DownOutlined,
    UserOutlined
  } from '@ant-design/icons-vue'
  import { useConfigStore } from '@/store/config'
  import { storeToRefs } from 'pinia'
  import { ServiceConfigVO, TypeConfigVO } from '@/api/config/types.ts'
  import { useComponentStore } from '@/store/component'
  import { HostComponentVO } from '@/api/component/types.ts'

  const route = useRoute()
  const configStore = useConfigStore()
  const { allConfigs } = storeToRefs(configStore)
  const componentStore = useComponentStore()
  const { hostComponents } = storeToRefs(componentStore)

  const serviceName = ref<string>(route.params.serviceName as string)

  // summary model start
  const currentHostComponent = ref<HostComponentVO[]>([])
  watch(hostComponents, (newVal) => {
    currentHostComponent.value = newVal.filter(
      (hc: HostComponentVO) => hc.serviceName === serviceName.value
    )
  })
  // summary model end

  // config model start
  const serviceConfigDesc = ref<SelectProps['options']>([])
  const activeSelect = ref()
  const currentConfigs = ref<TypeConfigVO[]>([])
  const currentConfigVersion = ref<number>()
  const initConfigVersion = ref<number>()
  const showConfigTip = ref<boolean>(false)

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
    currentConfigVersion.value = serviceConfigDesc.value?.[0].value as number

    currentConfigs.value = allConfigs.value
      .filter(
        (sc: ServiceConfigVO) =>
          sc.serviceName === serviceName.value &&
          currentConfigVersion.value === sc.version
      )
      .flatMap((sc: ServiceConfigVO) => sc.configs)
      .map((cd: TypeConfigVO) => ({
        typeName: cd.typeName,
        version: cd.version,
        properties: cd.properties
      }))
  }

  const handleChange: SelectProps['onChange'] = (value) => {
    if (typeof value === 'object' && 'key' in value) {
      currentConfigVersion.value = Number(value.key)
    }
    showConfigTip.value = currentConfigVersion.value !== initConfigVersion.value

    loadCurrentConfigs()
  }

  const activeConfig = ref()

  watch(currentConfigs, (newVal) => {
    activeConfig.value = newVal.length > 0 ? newVal[0].typeName : null
  })
  // config model end
  const handleMenuClick: MenuProps['onClick'] = (e) => {
    console.log('click', e)
  }

  const initServiceMeta = async () => {
    await configStore.loadLatestConfigs()
    await configStore.loadAllConfigs()
    activeSelect.value = serviceConfigDesc.value?.[0]
    currentConfigVersion.value = serviceConfigDesc.value?.[0].value as number
    initConfigVersion.value = serviceConfigDesc.value?.[0].value as number
    await componentStore.loadHostComponents()
  }

  onMounted(() => {
    initServiceMeta()
  })

  watch(
    () => route.params,
    (params) => {
      serviceName.value = params.serviceName as string
      initServiceMeta()
    }
  )
</script>

<template>
  <a-tabs>
    <template #rightExtra>
      <a-dropdown>
        <template #overlay>
          <a-menu @click="handleMenuClick">
            <a-menu-item key="1">
              <UserOutlined />
              Start
            </a-menu-item>
            <a-menu-item key="2">
              <UserOutlined />
              Stop
            </a-menu-item>
            <a-menu-item key="3">
              <UserOutlined />
              Restart
            </a-menu-item>
          </a-menu>
        </template>
        <a-button type="primary">
          {{ $t('common.action') }}
          <DownOutlined />
        </a-button>
      </a-dropdown>
    </template>
    <a-tab-pane key="summary">
      <template #tab>{{ $t('service.summary') }}</template>
      <a-layout-content class="summary-layout">
        <div class="left-div">
          <a-card>
            <template #title>{{ $t('service.components') }}</template>
            <template v-if="currentHostComponent.length > 0">
              <div class="summary-div">
                <template
                  v-for="item in currentHostComponent"
                  :key="`${item.id}`"
                >
                  <a-card class="card" :bordered="false" size="small">
                    <template #title>
                      <router-link :to="'/services/' + serviceName">
                        {{ item.displayName }}
                      </router-link>
                    </template>
                    <template #extra>
                      <a-tag>{{ item.category }}</a-tag>
                    </template>
                    <p>{{ item.hostname }}</p>
                    <p>
                      <CheckCircleTwoTone
                        v-if="item.state === 'STARTED'"
                        two-tone-color="#52c41a"
                      />
                      <MinusCircleTwoTone
                        v-else-if="item.state === 'MAINTAINED'"
                        two-tone-color="orange"
                      />
                      <CloseCircleTwoTone v-else two-tone-color="red" />
                      {{ item.state }}
                    </p>
                  </a-card>
                </template>
              </div>
            </template>
          </a-card>
        </div>
        <div class="middle-div"></div>
        <div class="right-div">
          <a-card>
            <template #title>{{ $t('service.quicklinks') }}</template>
            <a-empty /> </a-card
        ></div>
      </a-layout-content>
    </a-tab-pane>

    <a-tab-pane key="config" force-render>
      <template #tab>{{ $t('service.config') }}</template>
      <a-space>
        <a-select
          v-model:value="activeSelect"
          label-in-value
          :options="serviceConfigDesc"
          @change="handleChange"
        ></a-select>
        <template v-if="showConfigTip">
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
  .summary-layout {
    display: flex;
    margin: 3px;

    .left-div {
      width: 74%;
    }

    .middle-div {
      width: 2%;
    }

    .right-div {
      width: 24%;
    }

    .summary-div {
      display: flex;
      flex-direction: row;
      justify-content: space-around;
      flex-wrap: wrap;

      .card {
        width: 30%;
        margin: 5px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
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
