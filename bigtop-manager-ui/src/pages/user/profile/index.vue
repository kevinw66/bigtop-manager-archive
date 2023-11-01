<script setup lang="ts">
  import { useUserStore } from '@/store/user'
  import { storeToRefs } from 'pinia'
  import { ref } from 'vue'
  import { UserReq } from '@/api/user/types.ts'
  import { FormInstance } from 'ant-design-vue'

  const userStore = useUserStore()
  const { userVO } = storeToRefs(userStore)
  const editUser = {} as UserReq
  const formRef = ref<FormInstance>()

  const loading = ref<boolean>(false)
  const open = ref<boolean>(false)

  const updateCurrentUser = async (userId: any, editUser: UserReq) => {
    if (typeof userId === 'number') {
      loading.value = true
      await userStore.updateUserInfo(userId, editUser)
      loading.value = false
      open.value = false
    }
    resetForm()
  }

  const resetForm = () => {
    formRef.value?.resetFields()
  }
</script>

<template>
  <a-descriptions :title="$t('user.profile')" bordered>
    <template #extra>
      <a-button type="primary" @click="open = true">
        {{ $t('common.edit') }}
      </a-button>
    </template>
    <a-descriptions-item :label="$t('user.username')" :span="3">
      {{ userVO?.username }}
    </a-descriptions-item>
    <a-descriptions-item :label="$t('user.nickname')" :span="3">
      {{ userVO?.nickname }}
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
    <a-modal
      v-model:open="open"
      :title="$t('common.edit') + $t('user.profile')"
      @cancel="resetForm()"
    >
      <br />
      <a-form
        ref="formRef"
        name="profileForm"
        :model="editUser"
        layout="vertical"
      >
        <a-form-item :label="$t('user.nickname')" name="nickname">
          <a-input v-model:value="editUser.nickname" allow-clear />
        </a-form-item>
      </a-form>
      <template #footer>
        <a-button
          key="back"
          @click="
            () => {
              resetForm()
              open = false
            }
          "
        >
          {{ $t('common.back') }}
        </a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="loading"
          html-type="submit"
          @click="updateCurrentUser(userVO?.id, editUser)"
        >
          {{ $t('common.submit') }}
        </a-button>
      </template>

      <br />
    </a-modal>
  </div>
</template>

<style scoped lang="scss"></style>
