<script setup lang="ts">
  // const serviceInfo = defineModel<any>('serviceInfo')
  // const disableButton = defineModel<boolean>('disableButton')

  const onNextStep = async () => {
    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('common.install') }}</div>
  </div>
</template>

<style scoped lang="scss"></style>
