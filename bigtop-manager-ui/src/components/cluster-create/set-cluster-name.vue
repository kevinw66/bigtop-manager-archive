<script setup lang="ts">
  import { SmileOutlined } from '@ant-design/icons-vue'

  const clusterInfo = defineModel<any>('clusterInfo')
</script>

<template>
  <div class="container">
    <a-result
      :title="$t('cluster.set_cluster_name_title')"
      :sub-title="$t('cluster.set_cluster_name_sub_title')"
    >
      <template #icon>
        <smile-outlined />
      </template>
      <template #extra>
        <a-input
          v-model:value="clusterInfo.clusterName"
          :placeholder="$t('cluster.set_cluster_name_input')"
        />
      </template>
    </a-result>
  </div>
</template>

<style scoped lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    align-content: center;
    height: 100%;
  }
</style>
