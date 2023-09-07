<script setup lang="ts">
  import { HomeOutlined } from '@ant-design/icons-vue'
</script>

<template>
  <a-layout-content class="container">
    <a-breadcrumb class="breadcrumb">
      <a-breadcrumb-item>
        <home-outlined />
      </a-breadcrumb-item>
      <a-breadcrumb-item
        v-for="(item, index) in $route.path.substring(1).split('/')"
        :key="index"
      >
        {{ item }}
      </a-breadcrumb-item>
    </a-breadcrumb>
    <div class="content">
      <router-view />
    </div>
  </a-layout-content>
</template>

<style scoped lang="scss">
  .container {
    margin: 0 1rem;

    .breadcrumb {
      margin: 1rem 0;
    }

    .content {
      padding: 1rem;
      background: #fff;
      min-height: 540px;
    }
  }
</style>
