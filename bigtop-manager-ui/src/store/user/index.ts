/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { defineStore, storeToRefs } from 'pinia'
import { getCurrentUser, updateUser } from '@/api/user'
import { ref, shallowRef, watch } from 'vue'
import { UserReq, UserVO } from '@/api/user/types.ts'
import { MenuItem } from '@/store/user/types.ts'
import { initialRoutes, layoutRoutes } from '@/router/routes.ts'
import { useClusterStore } from '@/store/cluster'
import { RouteRecordRaw } from 'vue-router'

export const useUserStore = defineStore(
  'user',
  () => {
    const userVO = shallowRef<UserVO>()
    const menuItems = ref<MenuItem[]>([])

    const clusterStore = useClusterStore()
    const { selectedCluster } = storeToRefs(clusterStore)
    watch(selectedCluster, async () => {
      await generateMenu()
    })

    const getUserInfo = async () => {
      userVO.value = await getCurrentUser()
    }

    const updateUserInfo = async (editUser: UserReq) => {
      await updateUser(editUser)
      await getUserInfo()
    }

    const initMenu = async (pages: RouteRecordRaw[]) => {
      const items: MenuItem[] = []
      pages.forEach((route) => {
        const menuItem: MenuItem = {
          key: route.meta?.title?.toLowerCase(),
          to: route.path,
          title: route.meta?.title,
          icon: route.meta?.icon
        }

        if (route.children !== undefined) {
          menuItem.children = []
          route.children.forEach((child) => {
            menuItem.children?.push({
              key: child.meta?.title?.toLowerCase(),
              to: route.path + child.path,
              title: child.meta?.title,
              icon: child.meta?.icon
            })
          })
        }

        items.push(menuItem)
      })

      return Promise.resolve(items)
    }

    const generateMenu = async () => {
      if (selectedCluster.value) {
        menuItems.value = await initMenu(layoutRoutes)
      } else {
        menuItems.value = await initMenu(initialRoutes)
      }

      return Promise.resolve()
    }

    const logout = async () => {
      userVO.value = undefined
      localStorage.removeItem('Token')
      sessionStorage.removeItem('Token')

      return Promise.resolve()
    }

    return {
      userVO,
      menuItems,
      getUserInfo,
      updateUserInfo,
      generateMenu,
      logout
    }
  },
  {
    persist: false
  }
)
