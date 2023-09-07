<script setup lang="ts">
  import {
    UserOutlined,
    ProfileOutlined,
    LogoutOutlined
  } from '@ant-design/icons-vue'
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
      <user-outlined />
      <div class="name">Administrator</div>
    </div>
    <template #overlay>
      <a-menu @click="handleClick">
        <a-menu-item key="about">
          <template #icon>
            <user-outlined />
          </template>
          {{ $t('user.about') }}
        </a-menu-item>
        <a-menu-item key="settings">
          <template #icon>
            <profile-outlined />
          </template>
          {{ $t('user.settings') }}
        </a-menu-item>
        <a-menu-divider />
        <a-menu-item key="logout">
          <template #icon>
            <logout-outlined />
          </template>
          {{ $t('user.logout') }}
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
    font-size: 16px;
    padding: 0 0.5rem;
    border-radius: 6px;
    cursor: pointer;
    height: 36px;

    &:hover {
      background-color: var(--hover-color);
    }

    .name {
      font-size: 14px;
      margin-left: 0.5rem;
      line-height: 36px;
    }
  }
</style>
