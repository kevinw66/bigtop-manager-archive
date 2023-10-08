<script setup lang="ts"></script>

<template>
  <div class="container">
    <a-result
      status="success"
      :title="$t('cluster.create_finish_title')"
      :sub-title="$t('cluster.create_finish_sub_title')"
    />
  </div>
</template>

<style scoped lang="scss">
  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    align-content: center;
    height: 100%;
  }
</style>
