<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import type { SelectProps, MenuProps } from 'ant-design-vue'
  import {
    QuestionCircleOutlined,
    DownOutlined,
    UserOutlined
  } from '@ant-design/icons-vue'
  import { useConfigStore } from '@/store/config'
  import { storeToRefs } from 'pinia'
  import { ServiceConfigVO, TypeConfigVO } from '@/api/config/types.ts'
  import { useComponentStore } from '@/store/component'
  import { HostComponentVO } from '@/api/component/types.ts'
  import DotState from '@/components/dot-state/index.vue'

  const menuOps = [
    {
      key: '1',
      dicText: 'Start'
    },
    {
      key: '2',
      dicText: 'Stop'
    },
    {
      key: '3',
      dicText: 'Restart'
    }
  ]

  const stateColor = {
    Installed: '#00c0b9',
    Started: '#00c0b9',
    Maintained: '#c68d0d',
    Uninstalled: '#f5222d',
    Stopped: '#f5222d'
  }

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
    allConfigs.value = newVal
    loadCurrentConfigs()
  })

  const loadCurrentConfigs = () => {
    serviceConfigDesc.value = allConfigs.value
      .filter((sc: ServiceConfigVO) => sc.serviceName === serviceName.value)
      .map((sc: ServiceConfigVO) => ({
        value: sc.version,
        label: `Version: ${sc.version}`,
        title: `${sc.configDesc}
        \n${sc.createTime}`
      }))

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
    await configStore.loadAllConfigs()
    showConfigTip.value = false
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
            <a-menu-item v-for="item in menuOps" :key="item.key">
              <UserOutlined />
              <span>{{ item.dicText }}</span>
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
        <div class="left-section">
          <h2>{{ $t('service.components') }}</h2>
          <div v-if="currentHostComponent.length > 0" class="summary-ctx">
            <template v-for="item in currentHostComponent" :key="`${item.id}`">
              <div class="card">
                <div class="service-name">
                  <router-link :to="'/services/' + serviceName">
                    {{ item.displayName }}
                  </router-link>
                </div>
                <div class="comp-info">
                  <div class="host-name">{{ item.hostname }}</div>
                </div>
                <footer>
                  <a-tag
                    :bordered="false"
                    style="color: rgb(145 134 134 / 90%)"
                  >
                    {{ item.category }}
                  </a-tag>
                  <div class="comp-state">
                    <dot-state :color="stateColor[item.state]" />
                  </div>
                </footer>
              </div>
            </template>
          </div>
        </div>
        <div class="middle-section"></div>
        <div class="right-section">
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

    .left-section {
      width: 74%;
      .a-card {
        background-color: rgb(128, 171, 209);
      }
    }

    .middle-section {
      width: 2%;
    }

    .right-section {
      width: 24%;
    }

    .summary-ctx {
      display: flex;
      flex-wrap: wrap;
      box-sizing: border-box;

      .card {
        margin: 12px;
        padding: 12px;
        border-radius: 8px;
        position: relative;
        flex: 0 1 calc((100% / 3) - 24px);
        min-width: calc((100% / 3) - 24px);
        border: 1px solid rgb(211 211 211 / 23%);
        &:hover {
          box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
          transform: scale(1.1);
          transition: all 0.4s;
        }
        .service-name {
          font-size: 1.06rem;
          font-weight: 600;
          margin-bottom: 6px;
        }
        .comp-info {
          @include flex(center);
          margin-bottom: 20px;
          .host-name {
            width: 80%;
            font-size: 0.8rem;
            color: #797878d0;
            font-weight: 500;
            flex: 1;
          }
        }
        footer {
          @include flex(space-between, center);
          .comp-state {
            font-size: 1rem;
            align-items: flex-end;
          }
        }
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
