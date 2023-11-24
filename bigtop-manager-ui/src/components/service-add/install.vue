<script setup lang="ts">
  const onNextStep = async () => {
    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>Install</template>

<style scoped lang="scss"></style>
