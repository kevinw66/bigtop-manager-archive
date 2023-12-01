<script setup lang="ts">
  // const serviceInfo = defineModel<any>('serviceInfo')

  const onNextStep = async () => {
    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('service.assign_components') }}</div>
  </div>
</template>

<style scoped lang="scss"></style>
