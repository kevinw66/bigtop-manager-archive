<script setup lang="ts">
  import { useUserStore } from '@/store/user'
  import { storeToRefs } from 'pinia'
  import { reactive, ref } from 'vue'
  import { message } from 'ant-design-vue'
  import { UserVO } from '@/api/user/types.ts'

  const userVO2: UserVO = reactive()
  const userStore = useUserStore()
  const { userVO } = storeToRefs(userStore)

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
      console.log(userVO)
      console.log(userVO._value.nickname)
      message.info(`sss ${userVO._value.nickname} , query database`)
      console.log('query userVO')
    }, 2000)
  }

  const handleCancel = () => {
    open.value = false
  }
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
          :model="userVO2"
          :label="$t('user.nickname')"
          name="nickname"
          :rules="[{ required: true, message: 'Please input your nickname!' }]"
        >
          <!--          <a-input v-model:value="userVO2.nickname"/>-->
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped lang="scss"></style>
