<script setup lang="ts">
  import {
    PieChartOutlined,
    DesktopOutlined,
    UserOutlined,
    TeamOutlined,
    FileOutlined
  } from '@ant-design/icons-vue'
  import { ref } from 'vue'
  import { useUIStore } from '@/store/ui'
  import { storeToRefs } from 'pinia'

  const uiStore = useUIStore()
  const { siderCollapsed } = storeToRefs(uiStore)

  const selectedKeys = ref<string[]>(['1'])
</script>

<template>
  <a-layout-sider v-model:collapsed="siderCollapsed" class="sider" width="235">
    <div class="header">
      <img class="header-logo" src="@/assets/logo.svg" alt="logo" />
      <div v-if="!siderCollapsed" class="header-title">Bigtop Manager</div>
    </div>
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
</template>

<style scoped lang="scss">
  .sider {
    .header {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 32px;
      margin: 1rem;

      .header-logo {
        height: 32px;
        width: 32px;
      }

      .header-title {
        color: #ccc;
        font-weight: bold;
        font-size: 16px;
        margin-left: 1rem;
      }
    }
  }

  .site-layout .site-layout-background {
    background: #fff;
  }
  [data-theme='dark'] .site-layout .site-layout-background {
    background: #141414;
  }
</style>
