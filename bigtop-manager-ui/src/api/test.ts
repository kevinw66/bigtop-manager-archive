import request from '@/api/request'

export function testApi() {
  return request({
    method: 'get',
    url: '/test'
  })
}
