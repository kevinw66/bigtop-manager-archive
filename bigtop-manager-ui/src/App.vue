<script setup lang="ts">
  import { useLocaleStore } from '@/store/locale/locale.ts'
  import { storeToRefs } from 'pinia'

  const localeStore = useLocaleStore()
  const { antd } = storeToRefs(localeStore)
</script>

<template>
  <a-config-provider :locale="antd">
    <a-app class="app">
      <router-view />
    </a-app>
  </a-config-provider>
</template>

<style scoped lang="scss">
  .app {
    height: 100%;
  }
</style>
