<script setup lang="ts">
  import { useRouter } from 'vue-router'

  const router = useRouter()
  function back() {
    router.replace({
      path: '/'
    })
  }
</script>

<template>
  <a-result status="404" title="404" :sub-title="$t('common.desc_404')">
    <template #extra>
      <a-button type="primary" @click="back">
        {{ $t('common.back_home') }}
      </a-button>
    </template>
  </a-result>
</template>
