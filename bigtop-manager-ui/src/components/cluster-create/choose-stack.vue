<script setup lang="ts">
  import { ref, onMounted, watch } from 'vue'
  import { useStackStore } from '@/store/stack'
  import { storeToRefs } from 'pinia'
  import { useI18n } from 'vue-i18n'
  import { StackOptionProps } from '@/store/stack/types.ts'

  const clusterInfo = defineModel<any>('clusterInfo')

  const { t } = useI18n()
  const stackStore = useStackStore()

  const selectedOption = ref<string[]>([])
  const serviceData = ref<any[]>([])
  const { stackOptions, stackServices } = storeToRefs(stackStore)

  watch(selectedOption, (val) => {
    clusterInfo.value.stackName = val[0]
    clusterInfo.value.stackVersion = val[1]

    serviceData.value = stackServices.value[val[0] + '-' + val[1]]
  })

  watch(stackStore.stackOptions, (val: StackOptionProps[]) => {
    selectedOption.value = [
      `${val?.[0]?.value}`,
      `${val?.[0]?.children?.[0]?.value}`
    ]
  })

  const serviceColumns = [
    {
      title: t('common.name'),
      dataIndex: 'displayName',
      key: 'displayName',
      align: 'center',
      ellipsis: true
    },
    {
      title: t('common.version'),
      dataIndex: 'serviceVersion',
      key: 'serviceVersion',
      align: 'center',
      ellipsis: true
    },
    {
      title: t('common.desc'),
      dataIndex: 'serviceDesc',
      key: 'serviceDesc',
      align: 'center',
      ellipsis: true
    }
  ]

  const onNextStep = async () => {
    return Promise.resolve(true)
  }

  onMounted(async () => {
    await stackStore.getStacks()
  })

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
