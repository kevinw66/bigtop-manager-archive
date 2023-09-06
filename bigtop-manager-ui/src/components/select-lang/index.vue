<script setup lang="ts">
  import { useLocaleStore } from '@/store/locale/locale.ts'
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
      <CarbonLanguage />
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
    font-size: 16px;
    cursor: pointer;
    line-height: 0;
    padding: 0 1rem;
  }
</style>
