<script setup lang="ts">
  import { onMounted, ref } from 'vue'
  import { create } from '@/api/cluster'

  const clusterInfo = defineModel<any>('clusterInfo')

  const hosts = ref<string>('')

  onMounted(async () => {
    if (clusterInfo.value.hostnames) {
      hosts.value = clusterInfo.value.hostnames.join('\n')
    }
  })

  const onNextStep = async () => {
    clusterInfo.value.hostnames = hosts.value
      .split('\n')
      .filter((item) => item !== '')

    try {
      const res = await create(clusterInfo.value)
      clusterInfo.value.jobId = res.id
    } catch (e) {
      console.log(e)
      return Promise.resolve(false)
    }

    return Promise.resolve(true)
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <div class="title">{{ $t('cluster.set_hosts') }}</div>
    <a-textarea
      v-model:value="hosts"
      :rows="18"
      :placeholder="$t('cluster.set_hosts_placeholder')"
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
