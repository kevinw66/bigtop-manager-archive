<script setup lang="ts">
import {useUserStore} from '@/store/user'
import {storeToRefs} from 'pinia'

const userStore = useUserStore()
const {userVO} = storeToRefs(userStore)

</script>

<template>
  <a-descriptions title="User Profile" bordered>
    <template #extra>
      <a-button type="primary">Edit</a-button>
    </template>
    <a-descriptions-item label="nickname" :span="3">{{ userVO?.nickname }}</a-descriptions-item>
    <a-descriptions-item label="username" :span="3">{{ userVO?.username }}</a-descriptions-item>
    <a-descriptions-item label="createTime" :span="3">{{ userVO?.createTime }}</a-descriptions-item>
    <a-descriptions-item label="updateTime" :span="3">{{ userVO?.updateTime }}</a-descriptions-item>
    <a-descriptions-item label="email" :span="3">xx@qq.com</a-descriptions-item>
    <a-descriptions-item label="Status" :span="3">{{ userVO?.status }}</a-descriptions-item>
  </a-descriptions>
</template>

<style scoped lang="scss"></style>