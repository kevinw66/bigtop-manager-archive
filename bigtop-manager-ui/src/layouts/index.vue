<script setup lang="ts">
  import LayoutFooter from '@/layouts/footer.vue'
  import LayoutContent from '@/layouts/content.vue'
  import LayoutHeader from '@/layouts/header.vue'
  import LayoutSider from '@/layouts/sider.vue'
  import { useUserStore } from '@/store/user'
  import { onMounted } from 'vue'

  onMounted(async () => {
    const userStore = useUserStore()
    await userStore.getUserInfo()
    await userStore.generateMenu()
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
