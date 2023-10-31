<script setup lang="ts">
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'
  import { ref } from 'vue'
  import ClusterCreate from '@/components/cluster-create/index.vue'

  const clusterStore = useClusterStore()
  const { selectedCluster } = storeToRefs(clusterStore)

  const open = ref(false)
</script>

<template>
  <a-button v-if="selectedCluster" type="primary">Click Me</a-button>
  <div v-else class="tour">
    <a-result
      :title="$t('cluster.not_exist_title')"
      :sub-title="$t('cluster.not_exist_sub_title')"
    >
      <template #extra>
        <a-button type="primary" size="large" @click="() => (open = true)">
          {{ $t('cluster.create') }}
        </a-button>
        <cluster-create v-model:open="open" />
      </template>
    </a-result>
  </div>
</template>

<style scoped lang="scss">
  .tour {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    align-content: center;
    min-height: 540px;
  }
</style>
