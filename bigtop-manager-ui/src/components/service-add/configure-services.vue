<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { storeToRefs } from 'pinia'
  import { useStackStore } from '@/store/stack'
  import { execCommand } from '@/api/command'
  import { QuestionCircleOutlined } from '@ant-design/icons-vue'
  import _ from 'lodash'

  const serviceInfo = defineModel<any>('serviceInfo')

  const stackStore = useStackStore()
  const { currentStack } = storeToRefs(stackStore)

  const activeServiceTab = ref(serviceInfo.value.serviceCommands[0].serviceName)
  const activeConfigTab = ref()

  const serviceNameToDisplayName = _.fromPairs(
    currentStack.value.services.map((v) => [v.serviceName, v.displayName])
  )

  const services = computed(() => {
    return serviceInfo.value.serviceCommands.map((item: any) => {
      return item.serviceName
    })
  })

  const configs = computed(() => {
    return serviceInfo.value.serviceCommands
      .map((item: any) => {
        return { [item.serviceName]: item.configs }
      })
      .reduce((acc: any, curr: any) => {
        return { ...acc, ...curr }
      }, {})
  })

  const onNextStep = async () => {
    try {
      const res = await execCommand(serviceInfo.value)
      serviceInfo.value.jobId = res.id
    } catch (e) {
      console.log(e)
      return Promise.resolve(false)
    }

    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('service.configure_services') }}</div>
    <a-tabs v-model:activeKey="activeServiceTab" class="content">
      <a-tab-pane
        v-for="service in services"
        :key="service"
        :tab="serviceNameToDisplayName[service]"
      >
        <a-collapse v-model:activeKey="activeConfigTab" ghost>
          <a-collapse-panel
            v-for="config in configs[activeServiceTab]"
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
