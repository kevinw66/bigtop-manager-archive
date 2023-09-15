<script setup lang="ts">
  import { onBeforeMount } from 'vue'
  import { test } from '@/api/login'

  onBeforeMount(() => {
    // const a = '1'
  })

  const clickButtom = async () => {
    try {
      await test()
    } catch (e) {
      // console.log(e)
    }
  }
</script>

<template>
  <a-button type="primary" @click="clickButtom">Click Me</a-button>
</template>

<style scoped lang="scss"></style>
