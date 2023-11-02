<script setup lang="ts">
  import { ref } from 'vue'
  import ClusterCreate from '@/components/cluster-create/index.vue'
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'

  const clusterStore = useClusterStore()
  const { selectedCluster } = storeToRefs(clusterStore)

  const createWindowOpened = ref(false)
  console.log('dsadasdasdasdsa')
</script>

<template>
  <a-dropdown placement="bottom">
    <div class="icon">
      <div class="name">{{ selectedCluster?.clusterName }}</div>
    </div>
    <template #overlay>
      <a-menu>
        <a-menu-item key="switch">
          {{ $t('cluster.switch') }}
        </a-menu-item>
        <a-menu-item key="create" @click="() => (createWindowOpened = true)">
          {{ $t('cluster.create') }}
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>

  <cluster-create v-model:open="createWindowOpened" />
</template>

<style lang="scss" scoped>
  .icon {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 0.5rem;
    border-radius: 6px;
    cursor: pointer;
    height: 36px;

    &:hover {
      background-color: var(--hover-color);
    }

    .name {
      font-size: 14px;
    }
  }
</style>
