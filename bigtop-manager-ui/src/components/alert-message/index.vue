<script setup lang="ts">
  import { BellOutlined } from '@ant-design/icons-vue'
</script>

<template>
  <div class="icon">
    <a-badge size="small" color="red" count="1">
      <bell-outlined />
    </a-badge>
  </div>
</template>

<style lang="scss" scoped>
  .icon {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
    cursor: pointer;
    border-radius: 50%;
    height: 36px;
    width: 36px;

    &:hover {
      background-color: var(--hover-color);
    }
  }
</style>
