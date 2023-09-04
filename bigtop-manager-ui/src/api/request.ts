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

import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'
import { message } from 'ant-design-vue'
import { ResponseEntity } from '@/api/types'
import router from '@/router'
import { useLocaleStore } from '@/store/locale/locale.ts'

const localeStore = useLocaleStore()

const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  withCredentials: true,
  timeout: 3000
})

request.interceptors.request.use(
  (
    config: InternalAxiosRequestConfig<any>
  ): InternalAxiosRequestConfig<any> => {
    config.headers = config.headers || {}

    config.headers['Accept-Language'] = (
      localeStore.getLocale ?? 'en_US'
    ).replace('_', '-')

    const token =
      localStorage.getItem('Token') ??
      sessionStorage.getItem('Token') ??
      undefined
    if (token) {
      config.headers['Token'] = token
    }

    return config
  }
)

request.interceptors.response.use(
  (res: AxiosResponse) => {
    const responseEntity: ResponseEntity = res.data
    if (responseEntity.code !== 0) {
      message.error(responseEntity.message)
      if (responseEntity.code === 10000) {
        router.push('/login')
      }

      return Promise.reject(responseEntity)
    } else {
      return responseEntity.data
    }
  },
  (error: AxiosError) => {
    message.error(error.message)
    return Promise.reject(error)
  }
)

export default request
