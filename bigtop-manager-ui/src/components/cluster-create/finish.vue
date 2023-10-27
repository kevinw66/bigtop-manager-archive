<script setup lang="ts">
  import { SmileOutlined, FrownOutlined } from '@ant-design/icons-vue'

  const clusterInfo = defineModel<any>('clusterInfo')
</script>

<template>
  <div class="container">
    <a-result
      v-if="clusterInfo.success"
      :title="$t('cluster.create_success_title')"
      :sub-title="$t('cluster.create_success_sub_title')"
    >
      <template #icon>
        <smile-outlined />
      </template>
    </a-result>
    <a-result
      v-else
      :title="$t('cluster.create_fail_title')"
      :sub-title="$t('cluster.create_fail_sub_title')"
    >
      <template #icon>
        <frown-outlined />
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
