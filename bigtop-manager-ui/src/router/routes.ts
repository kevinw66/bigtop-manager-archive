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

import { h } from 'vue'
import { RouteRecordRaw } from 'vue-router'
import {
  AppstoreOutlined,
  DesktopOutlined,
  PieChartOutlined,
  SettingOutlined,
  UserOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import CircleFilled from '@/components/icons/circle-filled.vue'

const initialRoutes: RouteRecordRaw[] = [
  {
    path: '/dashboard',
    component: () => import('@/pages/dashboard/index.vue'),
    meta: {
      title: 'Dashboard',
      icon: h(PieChartOutlined)
    }
  }
]

const layoutRoutes: RouteRecordRaw[] = [
  ...initialRoutes,
  {
    path: '/hosts',
    component: () => import('@/pages/hosts/index.vue'),
    meta: {
      title: 'Hosts',
      icon: h(DesktopOutlined)
    }
  },
  {
    path: '/services/',
    component: () => import('@/pages/hosts/index.vue'),
    meta: {
      title: 'Services',
      icon: h(AppstoreOutlined)
    },
    children: [
      {
        path: 'zookeeper',
        component: () => import('@/pages/hosts/index.vue'),
        children: [],
        meta: {
          title: 'ZooKeeper',
          icon: h(CircleFilled, {
            style: 'font-size: 8px; color: #52c41a; margin-right: 0.5rem;'
          })
        }
      }
    ]
  }
]

const notDisplayedRoutes = [
  {
    path: '/user/',
    meta: {
      title: 'user',
      icon: h(UserOutlined)
    },
    children: [
      {
        path: 'profile',
        component: () => import('@/pages/user/profile/index.vue'),
        meta: {
          title: 'Profile',
          icon: h(ProfileOutlined)
        }
      },
      {
        path: 'settings',
        component: () => import('@/pages/user/settings/index.vue'),
        meta: {
          title: 'Settings',
          icon: h(SettingOutlined)
        }
      }
    ]
  }
]

const routes: RouteRecordRaw[] = [
  { path: '/login', component: () => import('@/pages/login/index.vue') },
  {
    path: '/',
    redirect: '/dashboard',
    component: () => import('@/layouts/index.vue'),
    children: [...layoutRoutes, ...notDisplayedRoutes]
  }
]

export { initialRoutes, layoutRoutes }
export default routes
