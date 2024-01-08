<script setup lang="ts">
  import { onMounted } from 'vue'
  import LayoutFooter from '@/layouts/footer.vue'
  import LayoutContent from '@/layouts/content.vue'
  import LayoutHeader from '@/layouts/header.vue'
  import LayoutSider from '@/layouts/sider.vue'
  import { useUserStore } from '@/store/user'
  import { useClusterStore } from '@/store/cluster'
  import { useServiceStore } from '@/store/service'
  import { useComponentStore } from '@/store/component'
  import { useConfigStore } from '@/store/config'

  const userStore = useUserStore()
  const clusterStore = useClusterStore()
  const serviceStore = useServiceStore()
  const componentStore = useComponentStore()
  const configStore = useConfigStore()

  onMounted(() => {
    userStore.getUserInfo()

    clusterStore.loadClusters()

    console.log('loading...')
    serviceStore.loadServices()
    componentStore.loadHostComponents()
    configStore.loadLatestConfigs()
  })
</script>

<template>
  <a-layout class="layout">
    <layout-sider />
    <a-layout>
      <layout-header />
      <layout-content />
      <layout-footer />
    </a-layout>
  </a-layout>
</template>

<style scoped lang="scss">
  .layout {
    min-height: 100vh;
  }
</style>
