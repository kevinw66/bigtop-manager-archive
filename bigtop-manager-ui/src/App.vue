<script setup lang="ts">
  import { watch, ref } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { useLocaleStore } from '@/store/locale/locale.ts'
  import en_US from 'ant-design-vue/es/locale/en_US'
  import zh_CN from 'ant-design-vue/es/locale/zh_CN'
  import dayjs from 'dayjs'
  import 'dayjs/locale/zh-cn'
  import 'dayjs/locale/en'

  const { locale } = useI18n()
  const localeStore = useLocaleStore()

  locale.value = localeStore.getLocale
  const antdLocale = ref(locale.value === 'en_US' ? en_US : zh_CN)

  watch(
    () => localeStore.locale,
    (newValue) => {
      locale.value = newValue
      antdLocale.value = newValue === 'en_US' ? en_US : zh_CN
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
