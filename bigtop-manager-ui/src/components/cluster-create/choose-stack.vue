<script setup lang="ts">
  import { ref, onMounted, watch } from 'vue'
  import { useStackStore } from '@/store/stack'
  import { storeToRefs } from 'pinia'
  import { useI18n } from 'vue-i18n'
  import { ServiceVO } from '@/api/service/types.ts'

  const clusterInfo = defineModel<any>('clusterInfo')

  const { t } = useI18n()
  const stackStore = useStackStore()

  const selectedOption = ref<string[]>([])
  const serviceData = ref<ServiceVO[]>([])
  const { stackOptions, stackServices } = storeToRefs(stackStore)

  watch(selectedOption, (val) => {
    clusterInfo.value.clusterCommand.stackName = val[0]
    clusterInfo.value.clusterCommand.stackVersion = val[1]
    clusterInfo.value.clusterCommand.fullStackName = val[0] + '-' + val[1]

    serviceData.value =
      stackServices.value[clusterInfo.value.clusterCommand.fullStackName]
  })

  const serviceColumns = [
    {
      title: t('common.name'),
      dataIndex: 'displayName',
      align: 'center',
      width: 150
    },
    {
      title: t('common.version'),
      dataIndex: 'serviceVersion',
      align: 'center',
      width: 150
    },
    {
      title: t('common.desc'),
      dataIndex: 'serviceDesc',
      align: 'center'
    }
  ]

  onMounted(async () => {
    if (clusterInfo.value.clusterCommand.fullStackName) {
      selectedOption.value = [
        clusterInfo.value.clusterCommand.stackName,
        clusterInfo.value.clusterCommand.stackVersion
      ]
    } else {
      selectedOption.value = [
        `${stackOptions.value?.[0]?.value}`,
        `${stackOptions.value?.[0]?.children?.[0]?.value}`
      ]
    }
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
    <div class="title">{{ $t('cluster.choose_stack') }}</div>
    <a-cascader
      v-model:value="selectedOption"
      :options="stackOptions"
      :placeholder="$t('common.select_tips')"
    />
    <div class="space" />
    <a-table
      :pagination="false"
      :scroll="{ y: 400 }"
      :columns="serviceColumns"
      :data-source="serviceData"
    />
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

    .space {
      margin: 1rem 0;
    }
  }
</style>
