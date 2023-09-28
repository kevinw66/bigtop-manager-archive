<script setup lang="ts">
  import { ref } from 'vue'
  import { SmileOutlined } from '@ant-design/icons-vue'

  const formRef = ref<any>(null)
  const clusterInfo = defineModel<any>('clusterInfo')

  const onNextStep = async () => {
    try {
      await formRef.value?.validate()
      return true
    } catch (e) {
      return false
    }
  }

  defineExpose({
    onNextStep
  })
</script>

<template>
  <div class="container">
    <a-result
      :title="$t('cluster.set_cluster_name_title')"
      :sub-title="$t('cluster.set_cluster_name_sub_title')"
    >
      <template #icon>
        <smile-outlined />
      </template>
      <template #extra>
        <a-form ref="formRef" :model="clusterInfo">
          <a-form-item
            name="clusterName"
            :rules="[
              {
                required: true,
                message: $t('cluster.set_cluster_name_valid')
              }
            ]"
          >
            <a-input
              v-model:value="clusterInfo.clusterName"
              allow-clear
              :placeholder="$t('cluster.set_cluster_name_input')"
            />
          </a-form-item>
        </a-form>
      </template>
    </a-result>
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
