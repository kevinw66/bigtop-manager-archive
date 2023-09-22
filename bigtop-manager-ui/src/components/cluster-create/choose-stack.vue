<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useStackStore } from '@/store/stack'
  import { storeToRefs } from 'pinia'

  const stackStore = useStackStore()

  const value = ref<string[]>([])
  const { stackOptions } = storeToRefs(stackStore)

  onMounted(async () => {
    await stackStore.getStacks()
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('cluster.choose_stack') }}</div>
    <div>{{ value }}</div>
    <a-cascader
      v-model:value="value"
      :options="stackOptions"
      :placeholder="$t('common.select_tips')"
    />
  </div>
</template>

<style scoped lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    align-content: center;
    height: 100%;

    .title {
      font-size: 1.5rem;
      line-height: 2rem;
      margin-bottom: 1rem;
    }
  }
</style>
