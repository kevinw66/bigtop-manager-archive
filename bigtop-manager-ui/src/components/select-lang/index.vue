<script setup lang="ts">
  import { useLocaleStore } from '@/store/locale'
  import { Locale } from '@/store/locale/types.ts'
  import CarbonLanguage from '@/components/icons/carbon-language.vue'
  import { storeToRefs } from 'pinia'

  const localeStore = useLocaleStore()
  const { locale } = storeToRefs(localeStore)

  const handleClick = ({ key }: { key: Locale }) => {
    localeStore.setLocale(key)
  }
</script>

<template>
  <a-dropdown placement="bottom">
    <div class="icon">
      <carbon-language />
    </div>
    <template #overlay>
      <a-menu :selected-keys="[locale]" @click="handleClick">
        <a-menu-item key="en_US">
          <template #icon>
            <span>🇺🇸</span>
          </template>
          English
        </a-menu-item>
        <a-menu-item key="zh_CN">
          <template #icon>
            <span>🇨🇳</span>
          </template>
          简体中文
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
    cursor: pointer;
    border-radius: 50%;
    height: 36px;
    width: 36px;

    &:hover {
      background-color: var(--hover-color);
    }
  }
</style>
