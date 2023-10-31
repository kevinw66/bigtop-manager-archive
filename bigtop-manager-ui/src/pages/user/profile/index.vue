<script setup lang="ts">
  import { useUserStore } from '@/store/user'
  import { storeToRefs } from 'pinia'
  import { ref } from 'vue'
  import { UserVO } from '@/api/user/types.ts'
  import {message} from "ant-design-vue";

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
      message.info(`submit ${editUser.nickname}`)
    }, 2000)
  }

  const handleCancel = () => {
    open.value = false
  }

  const userStore = useUserStore()
  const { userVO } = storeToRefs(userStore)
  const editUser: UserVO = <UserVO>{}
</script>

<template>
  <a-descriptions :title="$t('user.profile')" bordered>
    <template #extra>
      <a-button type="primary" @click="showModal">
        {{ $t('common.edit') }}
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
    <a-descriptions-item :label="$t('common.status')" :span="3">
      {{ userVO?.status }}
    </a-descriptions-item>
  </a-descriptions>
  <div>
    <a-modal v-model:open="open" :title="$t('common.edit')" @ok="handleOk">
      <template #footer>
        <a-button key="back" @click="handleCancel">
          {{ $t('common.back') }}
        </a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="loading"
          html-type="submit"
          @click="handleOk"
        >
          {{ $t('common.submit') }}
        </a-button>
      </template>

      <a-form name="basic" autocomplete="off" :model="editUser" layout="vertical">
        <a-form-item
          :label="$t('user.nickname')"
          name="nickname"
        >
          <a-input v-model:value="editUser.nickname" v-if="userVO !== undefined" :placeholder="userVO.nickname" />
          <a-input v-model:value="editUser.nickname" v-else />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped lang="scss"></style>
