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

import {
  createRouter,
  createWebHistory,
  RouteLocationNormalized
} from 'vue-router'
import routes from './routes'
import { useServiceStore } from '@/store/service'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/store/user'
import { useClusterStore } from '@/store/cluster'

const router = createRouter({
  routes,
  history: createWebHistory(import.meta.env.VITE_APP_BASE)
})

// When refresh on services page
// We cannot get installed services data
// Need to load cluster and services by ourselves
const loadServicesIfRefresh = async () => {
  const serviceStore = useServiceStore()
  const { loadingServices } = storeToRefs(serviceStore)
  if (loadingServices.value) {
    const clusterStore = useClusterStore()
    await clusterStore.loadClusters()
    await serviceStore.loadServices()
  }
}

const genericDynamicRouteAndRedirect = (to: RouteLocationNormalized) => {
  const userStore = useUserStore()
  const serviceStore = useServiceStore()
  const { installedServices } = storeToRefs(serviceStore)

  const modules = import.meta.glob('@/pages/**/**.vue')
  const serviceRoutes = installedServices.value.map((service) => ({
    name: service.serviceName,
    path: service.serviceName,
    component: modules['/src/pages/service/index.vue']
  }))

  serviceRoutes.forEach((route) => {
    if (router.hasRoute(route.name)) {
      router.removeRoute(route.name)
    }

    router.addRoute('services', route)
  })

  userStore.setMenuUpdated(false)
  return { ...to, replace: true }
}

// router.beforeEach(async (to, from) => {
//   const userStore = useUserStore()
//   const { menuUpdated } = storeToRefs(userStore)
//   if (!menuUpdated.value) {
//     // Menu is not updated, don't do anything
//     return true
//   }
//
//   console.log(to)
//   if (to.fullPath.split('/')[1] === 'services' && from.fullPath === '/') {
//     // Refresh in service page
//     await loadServicesIfRefresh()
//   }
//
//   return genericDynamicRouteAndRedirect(to)
// })

export default router
