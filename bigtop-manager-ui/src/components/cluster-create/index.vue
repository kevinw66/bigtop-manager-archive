<script setup lang="ts">
  import { ref, reactive, h, watch, onMounted } from 'vue'
  import { Modal } from 'ant-design-vue'
  import { ExclamationCircleFilled } from '@ant-design/icons-vue'
  import { useI18n } from 'vue-i18n'
  import { useStackStore } from '@/store/stack'
  import SetClusterName from './set-cluster-name.vue'
  import ChooseStack from './choose-stack.vue'
  import SetRepository from './set-repository.vue'
  import SetHosts from './set-hosts.vue'
  import Install from './install.vue'
  import Finish from './finish.vue'
  import { useClusterStore } from '@/store/cluster'

  const open = defineModel<boolean>('open')
  const { t, locale } = useI18n()
  const stackStore = useStackStore()
  const clusterStore = useClusterStore()

  const initItems = () => [
    {
      disabled: true,
      status: 'process',
      title: t('cluster.set_cluster_name'),
      content: h(SetClusterName)
    },
    {
      disabled: true,
      status: 'wait',
      title: t('cluster.choose_stack'),
      content: h(ChooseStack)
    },
    {
      disabled: true,
      status: 'wait',
      title: t('cluster.set_repository'),
      content: h(SetRepository)
    },
    {
      disabled: true,
      status: 'wait',
      title: t('cluster.set_hosts'),
      content: h(SetHosts)
    },
    {
      disabled: true,
      status: 'wait',
      title: t('common.install'),
      content: h(Install)
    },
    {
      disabled: true,
      status: 'wait',
      title: t('common.finish'),
      content: h(Finish)
    }
  ]

  const initClusterInfo = () => {
    const clusterCommand = {
      clusterName: '',
      // 1-Physical Machine 2-Kubernetes
      // Only support physical machine right now
      clusterType: 1,
      stackName: '',
      stackVersion: '',
      fullStackName: '',
      repoInfoList: [],
      hostnames: []
    }

    return {
      command: 'install',
      commandLevel: 'cluster',
      clusterCommand: clusterCommand,
      // Related job id
      jobId: 0,
      // Job Status
      success: false
    }
  }

  const current = ref<number>(0)
  const items = reactive(initItems())
  const clusterInfo = reactive(initClusterInfo())
  const disableButton = ref<boolean>(false)
  const currentItemRef = ref<any>(null)
  const loadingNext = ref<boolean>(false)
  watch(locale, () => {
    Object.assign(items, initItems())
  })

  const next = async () => {
    loadingNext.value = true
    try {
      const valid = await currentItemRef.value?.onNextStep()
      if (valid) {
        console.log('clusterInfo:', JSON.stringify(clusterInfo))
        items[current.value].status = 'finish'
        current.value++
        items[current.value].status = 'process'
      }
    } finally {
      loadingNext.value = false
    }
  }

  const prev = () => {
    items[current.value].status = 'wait'
    current.value--
    items[current.value].status = 'process'
  }

  const clear = () => {
    // Reload clusters
    clusterStore.loadClusters()

    // Clear status
    current.value = 0
    open.value = false
    Object.assign(items, initItems())
    Object.assign(clusterInfo, initClusterInfo())
  }

  const cancel = () => {
    Modal.confirm({
      title: t('common.exit'),
      icon: h(ExclamationCircleFilled),
      content: t('common.exit_confirm'),
      onOk() {
        clear()
      }
    })
  }

  onMounted(async () => {
    await stackStore.initStacks()
  })
</script>

<template>
  <a-modal
    :open="open"
    width="95%"
    centered
    destroy-on-close
    :mask-closable="false"
    :keyboard="false"
    @update:open="cancel"
  >
    <template #footer>
      <a-button
        v-if="current > 0"
        class="footer-btn"
        type="primary"
        :disabled="disableButton"
        @click="prev"
      >
        {{ $t('common.prev') }}
      </a-button>
      <a-button
        v-if="current < items.length - 1"
        class="footer-btn"
        type="primary"
        :loading="loadingNext"
        :disabled="disableButton"
        @click="next"
      >
        {{ $t('common.next') }}
      </a-button>
      <a-button
        v-if="current === items.length - 1"
        class="footer-btn"
        type="primary"
        @click="() => clear()"
      >
        {{ $t('common.done') }}
      </a-button>
    </template>
    <div class="container">
      <a-steps
        v-model:current="current"
        class="step"
        direction="vertical"
        size="small"
        :items="items"
      />
      <div class="content">
        <component
          :is="items[current].content"
          ref="currentItemRef"
          v-model:clusterInfo="clusterInfo"
          v-model:disableButton="disableButton"
        />
      </div>
    </div>
  </a-modal>
</template>

<style scoped lang="scss">
  .container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;

    .step {
      width: 15%;
      height: 35rem;
    }

    .content {
      margin-top: 1rem;
      padding-left: 1rem;
      height: 35rem;
      width: 85%;
      text-align: center;
      border-left: 1px solid #d9d9d9;
    }
  }

  .footer-btn {
    width: 8.333333%;
  }
</style>
