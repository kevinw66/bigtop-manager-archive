<script setup lang="ts">
  import {
    PieChartOutlined,
    DesktopOutlined,
    UserOutlined,
    TeamOutlined,
    FileOutlined
  } from '@ant-design/icons-vue'
  import { ref } from 'vue'
  import LayoutFooter from '@/layouts/footer.vue'
  import LayoutContent from '@/layouts/content.vue'
  import LayoutHeader from '@/layouts/header.vue'

  const collapsed = ref<boolean>(false)
  const selectedKeys = ref<string[]>(['1'])
</script>

<template>
  <a-layout style="min-height: 100vh">
    <a-layout-sider v-model:collapsed="collapsed" collapsible>
      <div class="logo" />
      <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
        <a-menu-item key="1">
          <pie-chart-outlined />
          <span><router-link to="/dashboard">Dashboard</router-link></span>
        </a-menu-item>
        <a-menu-item key="2">
          <desktop-outlined />
          <span><router-link to="/admin">Admin</router-link></span>
        </a-menu-item>
        <a-sub-menu key="sub1">
          <template #title>
            <span>
              <user-outlined />
              <span>User</span>
            </span>
          </template>
          <a-menu-item key="3">Tom</a-menu-item>
          <a-menu-item key="4">Bill</a-menu-item>
          <a-menu-item key="5">Alex</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub2">
          <template #title>
            <span>
              <team-outlined />
              <span>Team</span>
            </span>
          </template>
          <a-menu-item key="6">Team 1</a-menu-item>
          <a-menu-item key="8">Team 2</a-menu-item>
        </a-sub-menu>
        <a-menu-item key="9">
          <file-outlined />
          <span>File</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <layout-header />
      <layout-content />
      <layout-footer />
    </a-layout>
  </a-layout>
</template>

<style scoped lang="scss">
  #components-layout-demo-side .logo {
    height: 32px;
    margin: 16px;
    background: rgba(255, 255, 255, 0.3);
  }

  .site-layout .site-layout-background {
    background: #fff;
  }
  [data-theme='dark'] .site-layout .site-layout-background {
    background: #141414;
  }
</style>
