<script setup lang="ts">
  import { ref } from 'vue'
  import { PlusOutlined, CaretRightOutlined } from '@ant-design/icons-vue'
  import SquareFilled from '@/components/icons/square-filled.vue'
  import ServiceAdd from '@/components/service-add/index.vue'

  const addWindowOpened = ref(false)
</script>

<template>
  <a-dropdown trigger="click" @click.stop>
    <a class="dropdown-text">···</a>
    <template #overlay>
      <a-menu>
        <a-menu-item key="start">
          <caret-right-outlined />
          {{ $t('service.start_all') }}
        </a-menu-item>
        <a-menu-item key="stop">
          <square-filled />
          {{ $t('service.stop_all') }}
        </a-menu-item>
        <a-menu-divider />
        <a-menu-item key="add" @click="() => (addWindowOpened = true)">
          <plus-outlined />
          {{ $t('service.add') }}
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>

  <suspense>
    <service-add v-model:open="addWindowOpened" />
  </suspense>
</template>

<style scoped lang="scss">
  .dropdown-text {
    font-size: 24px;
    color: white;
  }
</style>
