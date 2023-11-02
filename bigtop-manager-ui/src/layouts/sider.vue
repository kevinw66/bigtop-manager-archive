<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue'
  import { useUIStore } from '@/store/ui'
  import { useUserStore } from '@/store/user'
  import { storeToRefs } from 'pinia'
  import { RouterLink, useRouter } from 'vue-router'

  const uiStore = useUIStore()
  const userStore = useUserStore()
  const router = useRouter()

  const { siderCollapsed } = storeToRefs(uiStore)
  const { menuItems } = storeToRefs(userStore)

  const selectedKeys = ref<string[]>([])

  const updateSideBar = () => {
    const splitPath = router.currentRoute.value.path.split('/')
    const selectedKey = splitPath[splitPath.length - 1]
    selectedKeys.value = [selectedKey]
  }

  watch(router.currentRoute, () => {
    updateSideBar()
  })

  onMounted(async () => {
    updateSideBar()
  })
</script>

<template>
  <a-layout-sider v-model:collapsed="siderCollapsed" class="sider" width="235">
    <div class="header">
      <img class="header-logo" src="@/assets/logo.svg" alt="logo" />
      <div v-if="!siderCollapsed" class="header-title">Bigtop Manager</div>
    </div>
    <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
      <template v-for="item in menuItems">
        <template v-if="item.children !== undefined">
          <a-sub-menu :key="item.key">
            <template #title>
              <span>
                <component :is="() => item.icon" />
                <span>
                  {{ item.title }}
                </span>
              </span>
            </template>
            <a-menu-item v-for="subItem in item.children" :key="subItem.key">
              <component :is="() => subItem.icon" />
              <span>
                <router-link :to="subItem.to">{{ subItem.title }}</router-link>
              </span>
            </a-menu-item>
          </a-sub-menu>
        </template>
        <template v-else>
          <a-menu-item :key="item.key">
            <component :is="() => item.icon" />
            <span>
              <router-link :to="item.to">{{ item.title }}</router-link>
            </span>
          </a-menu-item>
        </template>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<style scoped lang="scss">
  .sider {
    .header {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 32px;
      margin: 1rem;

      .header-logo {
        height: 32px;
        width: 32px;
      }

      .header-title {
        color: #ccc;
        font-weight: bold;
        font-size: 16px;
        margin-left: 1rem;
      }
    }
  }
</style>
