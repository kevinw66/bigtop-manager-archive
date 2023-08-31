<script setup lang="ts">
  import { useLocalesStore } from '@/store/locales/locales'
  import CarbonLanguage from '@/components/icons/carbon-language.vue'
  import { Locales } from '@/store/locales/types'

  const localesStore = useLocalesStore()

  const changeLocales = ({ key }: { key: string }) => {
    localesStore.setLocales(key as Locales)
  }
</script>

<template>
  <a-dropdown placement="bottom">
    <div class="icon">
      <CarbonLanguage />
    </div>
    <template #overlay>
      <a-menu :selected-keys="[localesStore.getLocales]" @click="changeLocales">
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
