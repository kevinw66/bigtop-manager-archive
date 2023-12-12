<script setup lang="ts">
  import { ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  const route = useRoute()

  const serviceName = ref<string>(route.params.serviceName as string)

  watch(
    () => route.params,
    (params) => {
      serviceName.value = params.serviceName as string
    }
  )
</script>

<template>
  <div>this is {{ serviceName }} page</div>
</template>

<style scoped lang="scss"></style>
