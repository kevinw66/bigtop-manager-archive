<script setup lang="ts">
  import { ref } from 'vue'
  import ClusterCreate from '@/components/cluster-create/index.vue'

  const open = ref(false)
</script>

<template>
  <div class="tour">
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
