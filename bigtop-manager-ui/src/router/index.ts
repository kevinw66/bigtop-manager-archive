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

import routes from './routes'
import { createRouter, createWebHistory } from 'vue-router'
import { useServiceStore } from '@/store/service'
import { storeToRefs } from 'pinia'
import { ServiceVO } from '@/api/service/types.ts'

const router = createRouter({
  routes,
  history: createWebHistory(import.meta.env.VITE_APP_BASE)
})

router.beforeEach((to) => {
  if (to.name === 'services') {
    const serviceStore = useServiceStore()
    const { installedServices } = storeToRefs(serviceStore)
    const installedServiceNames = installedServices.value.map(
      (service: ServiceVO) => service.serviceName
    )

    if (!installedServiceNames.includes(to.params.serviceName as string)) {
      return '/404'
    }
  }
})

export default router
