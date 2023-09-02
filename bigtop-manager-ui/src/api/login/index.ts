import { LoginReq, LoginRes } from '@/api/login/types.ts'
import request from '@/api/request.ts'

export const login = (data: LoginReq): Promise<LoginRes> => {
  return request({
    method: 'post',
    url: '/login',
    data
  })
}

export const test = () => {
  return request({
    method: 'get',
    url: '/test'
  })
}
