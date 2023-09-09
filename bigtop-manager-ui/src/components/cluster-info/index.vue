<script setup lang="ts">
  import { message } from 'ant-design-vue'

  const handleClick = ({ key }: { key: string }) => {
    if (key === 'about' || key === 'settings') {
      message.info('Coming soon!')
    }
  }
</script>

<template>
  <a-dropdown placement="bottom">
    <div class="icon">
      <div class="name">Cluster A</div>
    </div>
    <template #overlay>
      <a-menu @click="handleClick">
        <a-menu-item key="switch">
          {{ $t('cluster.switch') }}
        </a-menu-item>
        <a-menu-item key="create">
          {{ $t('cluster.create') }}
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
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
