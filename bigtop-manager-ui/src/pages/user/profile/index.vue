<script setup lang="ts">
  import { useUserStore } from '@/store/user'
  import { storeToRefs } from 'pinia'
  import { ref, reactive } from 'vue'

  const loading = ref<boolean>(false)
  const open = ref<boolean>(false)

  const showModal = () => {
    open.value = true
  }

  const handleOk = () => {
    loading.value = true
    setTimeout(() => {
      loading.value = false
      open.value = false
    }, 2000)
  }

  const handleCancel = () => {
    open.value = false
  }

  const userStore = useUserStore()
  const { userVO } = storeToRefs(userStore)

  interface FormState {
    username: string
    email: string
  }

  const formState = reactive<FormState>({
    username: '',
    email: ''
  })
</script>

<template>
  <a-descriptions :title="$t('user.profile')" bordered>
    <template #extra>
      <a-button type="primary" @click="showModal">
        {{ $t('user.edit') }}
      </a-button>
    </template>
    <a-descriptions-item :label="$t('user.nickname')" :span="3">
      {{ userVO?.nickname }}
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.username')" :span="3">
      {{ userVO?.username }}
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.create_time')" :span="3">
      {{ userVO?.createTime }}
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.update_time')" :span="3">
      {{ userVO?.updateTime }}
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.email')" :span="3">
      xx@qq.com
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.status')" :span="3">
      {{ userVO?.status }}
    </a-descriptions-item>
  </a-descriptions>
  <div>
    <a-modal v-model:open="open" :title="$t('user.edit')" @ok="handleOk">
      <template #footer>
        <a-button key="back" @click="handleCancel">
          {{ $t('user.back') }}
        </a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="loading"
          html-type="submit"
          @click="handleOk"
        >
          {{ $t('user.submit') }}
        </a-button>
      </template>

      <a-form name="basic" autocomplete="off">
        <a-form-item
          :label="$t('user.nickname')"
          name="nickname"
          :rules="[{ required: true, message: 'Please input your nickname!' }]"
        >
          <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
          :label="$t('user.email')"
          name="email"
          :rules="[{ type: 'email' }]"
        >
          <a-input v-model:value="formState.email" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped lang="scss"></style>
