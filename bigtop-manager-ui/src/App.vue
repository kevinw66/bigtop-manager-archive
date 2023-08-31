<script setup lang="ts">
  import { watch, ref } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { useLocalesStore } from '@/store/locales/locales'
  import en_US from 'ant-design-vue/es/locale/en_US'
  import zh_CN from 'ant-design-vue/es/locale/zh_CN'
  import dayjs from 'dayjs'
  import 'dayjs/locale/zh-cn'
  import 'dayjs/locale/en'

  const { locale } = useI18n()
  const localesStore = useLocalesStore()
  const locales = localesStore.getLocales

  locale.value = locales
  const antdLocale = ref(locales === 'en_US' ? en_US : zh_CN)

  watch(
    () => localesStore.locales,
    (locales) => {
      locale.value = locales
      antdLocale.value = locales === 'en_US' ? en_US : zh_CN
      dayjs.locale(antdLocale.value.locale)
    }
  )
</script>

<template>
  <a-config-provider :locale="antdLocale">
    <a-app class="app">
      <router-view />
    </a-app>
  </a-config-provider>
</template>

<style lang="scss" scoped>
  .app {
    height: 100%;
  }
</style>
