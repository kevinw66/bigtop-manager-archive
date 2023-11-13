<script setup lang="ts">
  import { useClusterStore } from '@/store/cluster'
  import { storeToRefs } from 'pinia'
  import Dashboard from '@/components/dashboard/index.vue'
  import Tips from '@/components/dashboard/tips.vue'

  const clusterStore = useClusterStore()
  const { selectedCluster } = storeToRefs(clusterStore)
</script>

<template>
  <dashboard v-if="selectedCluster" />
  <tips v-else />
</template>

<style scoped lang="scss"></style>
