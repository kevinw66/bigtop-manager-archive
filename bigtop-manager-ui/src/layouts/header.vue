<script setup lang="ts">
  import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue'
  import ClusterInfo from '@/components/cluster-info/index.vue'
  import JobInfo from '@/components/job-info/index.vue'
  import AlertInfo from '@/components/alert-info/index.vue'
  import SelectLang from '@/components/select-lang/index.vue'
  import UserAvatar from '@/components/user-avatar/index.vue'
  import { useUIStore } from '@/store/ui'
  import { storeToRefs } from 'pinia'
  import { useClusterStore } from '@/store/cluster'

  const uiStore = useUIStore()
  const clusterStore = useClusterStore()
  const { siderCollapsed } = storeToRefs(uiStore)
  const { clusters } = storeToRefs(clusterStore)
</script>

<template>
  <a-layout-header class="header">
    <div class="header-left">
      <menu-unfold-outlined
        v-if="siderCollapsed"
        @click="uiStore.changeCollapsed"
      />
      <menu-fold-outlined v-else @click="uiStore.changeCollapsed" />
    </div>
    <div class="header-right">
      <cluster-info v-if="clusters.length > 0" />
      <job-info />
      <alert-info />
      <select-lang />
      <user-avatar />
    </div>
  </a-layout-header>
</template>

<style scoped lang="scss">
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    padding: 0 1rem;
    height: 48px;

    .header-left {
      font-size: 16px;
      cursor: pointer;
      transition: color 0.3s;
    }

    .header-right {
      display: flex;
      justify-content: end;
      align-items: center;
    }
  }
</style>
